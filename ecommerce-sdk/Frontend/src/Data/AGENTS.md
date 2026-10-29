# AGENTS.md

Two independent apps in one folder. There is no workspace tooling, no root build, and no
orchestration — you run each side separately.

| Path | Stack | Entry |
|---|---|---|
| `Backend/commerce` | Spring Boot 4.1.1, Java 25, Maven (`com.oneteamplus.commerce`) | `CommerceApplication.java` |
| `Frontend` | CRA 5 (`react-scripts`), React 19, react-router-dom 7, Redux | `src/index.js` |

## Commands

```bash
# Backend — ALWAYS use ./mvnw, NOT `mvn` (system mvn is 3.8.7; wrapper pins 3.9.16)
cd Backend/commerce
./mvnw spring-boot:run          # runs tests first; add -DskipTests to skip
./mvnw test                      # only test is a context-load smoke test
./mvnw test -Dtest=CommerceApplicationTests
./mvnw test -Dtest=CommerceApplicationTests#contextLoads

# Frontend
cd Frontend
npm start                        # dev server on :3000
CI=true npm test                 # CI=true is required or jest hangs in watch mode
CI=true npm test -- src/App.test.js
```

No linter, formatter, or typechecker is configured for the backend. Frontend lints only via
CRA's `react-app` eslint config, implicitly during `npm start`/`npm test`.

## Setup prerequisites

- **MySQL must be running** at `localhost:3306`, database `ecommerce`, user `omaima` / `omaima.OK16`.
  `ddl-auto=update`, so Hibernate creates the schema. There is no migration tool, no seed script,
  and no Docker Compose — nothing provisions this for you.
- **Keystores must exist** at `/work/e-commerce/Backend/promo-engine-dev.p12` and
  `/work/e-commerce/Backend/truststore.p12` (password `changeit` for both). Paths are absolute
  and machine-specific in `application.properties`; `RestTemplateConfig` binds the truststore as a
  `Resource`, so a missing file **fails the app context at startup**. The app will not start
  anywhere else without editing those two lines.
- `Backend/truststore.p12` pins the private CA for the external SuperPay payment gateway, and
  `disableRedirectHandling()` is required so the `Location` response header survives. Don't
  "simplify" `RestTemplateConfig` to a default `RestTemplate` — payment link creation breaks.

## Landmines

- **`axios` is not in `Frontend/package.json`.** It resolves only from `/work/e-commerce/node_modules`,
  via webpack's upward `node_modules` walk past `Frontend/`. The root `package.json` exists solely
  to host that folder (it has no `scripts`). Running `npm ci` / `npm prune` / `rm -rf` at the repo
  root, or a standalone `npm install` in `Frontend/`, breaks the build with "Module not found:
  axios". Fix properly by adding `axios` to `Frontend/package.json`.
- **The backend serves HTTPS, not HTTP.** `server.ssl.key-store` is set, so port 5454 is TLS-only;
  `http://localhost:5454` fails. The frontend hardcodes `https://localhost:5454` in
  `src/config/apiConfig.js` and there is **no env-var mechanism anywhere** — no `.env`, no
  `REACT_APP_*`, no `proxy` field. Changing the backend port means editing that file.
- **The only test, `CommerceApplicationTests#contextLoads`, is a full `@SpringBootTest`** with no
  H2, no Testcontainers, and no test profile — so it boots the real context and needs live MySQL
  plus both keystores. It is not a fast unit check.
- **`Frontend/src/App.test.js` is unmodified CRA boilerplate and cannot pass** — it looks for a
  "learn react" link that was removed, and renders `<App />` without a Router or Redux Provider.
  Expect `npm test` to fail; don't read that failure as a regression you caused.

## Backend conventions

- Services are `XxxService` interface + `XxxServiceImplementation` class **in the same package**
  (no `impl` subpackage). Constructor injection everywhere; no `@Autowired` fields.
- **Two DTO packages with opposite meanings.** `payload.request` holds inbound API request bodies;
  `dto` holds *outbound* payment-gateway JSON (`dto.OrderDetails` is not the order DTO).
- Success envelope is `ApiResponse{Boolean status, String message}`; failures mostly surface as
  **HTTP 500** because there is no `@ControllerAdvice` anywhere.
- `@Valid` is never used, despite validation being on the classpath. Several controllers bind JPA
  entities directly as request bodies (`/auth/signup`, `POST /api/orders/`,
  `PUT /api/cart_items/{id}`, admin product update).
- **Trailing slashes are load-bearing.** Boot disables trailing-slash matching, so `GET /api/cart`
  404s but `GET /api/cart/` works. The frontend already sends the slashes. Change both sides or
  neither.
- `open-in-view=true` is set deliberately, so lazy loading during JSON serialization is load-bearing
  (hence the explicit `Hibernate.initialize` calls in `UserServiceImplementation`).
- `ddl-auto=update` means entity edits mutate the live schema. Renaming a field renames a column.

## Auth model (surprising in ways that matter)

- Security config is a single `SecurityFilterChain` in `config/AppConfig.java`. Rule is
  `/api/** → authenticated()`, everything else `permitAll()`. **There is no role-based access
  control at all** — no `@PreAuthorize`, no `hasRole`. The admin controllers
  (`/api/admin/orders`, `/api/admin/products`) are protected by URL prefix only. `User.role` is
  never written or read.
- The JWT contains a **single claim: `email`**. `JwtValidator` reads a `claims.get("authorities")`
  that the provider never sets, so it authenticates everyone with a bogus authority named `"null"`.
  Adding a `hasRole(...)` check will not work until that claim is actually issued.
- **The current user is not read from `SecurityContextHolder`.** ~18 controllers take
  `@RequestHeader("Authorization") String jwt` and re-parse it via
  `userService.findUserProfileByJwt(jwt)`. Because that header is `required = true`, a missing one
  yields **400, not 401**. Follow the existing pattern unless you're deliberately changing auth.
- `JwtValidator` throws on an invalid token, and it is inserted *upstream* of
  `ExceptionTranslationFilter`, so a bad token returns **500**, not 401 — and it breaks even
  public endpoints.
- The JWT signing secret is a hardcoded `static final` in `config/JwtConstant.java`, not a
  property, so it can't be overridden per environment.
- `/api/payments/webhook/*` sits under `/api/**` and therefore **requires a JWT**, but the external
  gateway calls it server-to-server without one. The handler also has an in-code `TODO` noting the
  payload signature is unverified.

## Secrets

Live credentials are committed in `Backend/commerce/src/main/resources/application.properties`:
MySQL password, SuperPay merchant API key, and SuperPay HMAC secret, plus keystore passwords. The
JWT secret is hardcoded in `JwtConstant.java`. There is no profile separation and no secrets
management — flag this if you touch config, but don't assume new secrets will land somewhere safer.

## Frontend conventions

- **No Redux Toolkit** — it is not a dependency. `src/State/store.js` hand-rolls
  `combineReducers` + `legacy_createStore` with `applyMiddleware(thunk)`. Slices are
  `ActionType.js` / `Action.js` / `Reducer.js` triplets. No `createAsyncThunk`, no selectors
  files; every component does an inline `useSelector(store => store)` and destructures the slice.
- Adding a slice means editing `store.js` too: `paymentReducer` was never registered, so
  `store.payment` is permanently `undefined` and its "Redirecting..." state never activates.
- `src/State/Payment/Reducer.js` imports `CREATE_PAYMENT_*` constants that don't exist in
  `ActionType.js`; it only survives because the reducer isn't mounted.
- Routing lives entirely in `src/Routers/CustomerRouters.jsx`, which doubles as the app layout
  (Navigation + Routes + Footer). `App.js` renders only `<Route path='/*' />`. There is no
  `/login` or `/register` page — both render `<HomePage/>`; auth happens via a MUI `Modal`
  (`customer/Auth/AuthModel.jsx`, note the typo).
- **Styling is a mix, and Tailwind dominates.** Tailwind utilities for layout/typography; MUI
  (`@mui/material` v9) for widgets only, via the `sx` prop. There is no `ThemeProvider`,
  `createTheme`, or `styled()`. Brand purple `#9155FD` is applied ad-hoc per component. Tailwind
  works because CRA auto-detects the presence of `tailwind.config.js` — there is no
  `postcss.config.js` to check. `src/App.css` is dead boilerplate.
- Export style is mixed (default vs named) per file, and page components live under
  `customer/components/`, not `customer/pages/`. Check the import site before changing exports.
- `src/Data/mens.js` is a ~44-item static fixture scraped from a competitor, with field typos baked
  in (`discountPersent`, `topLavelCategory`, …). It is not synced with the backend `Product` entity.

## Don't be misled by dead code

These look functional but are stubs — don't build on them:

- `controller/HomeController.java` and `service/CategoryService.java` are empty and unannotated, so
  they aren't Spring beans at all.
- `ProductService.findProductByCategory` returns `List.of()`.
- Razorpay fields on `PaymentDetails` and a commented-out Razorpay handler in `PaymentController`
  are leftovers from a replaced provider; SuperPay is live.
- `AdminProductController.updateProduct` is **broken**: `productId` lacks `@PathVariable`, so it
  binds to `null` and NPEs.
- `CartItemServiceImplementation.createCartItem` hardcodes `quantity = 1` and discards
  `AddItemRequest.quantity`, so cart quantity never changes from the API.
- `PaymentWebhookController` + `Order.jsx`/`OrderDetails.jsx`/`ProductDetails.jsx` contain
  hardcoded placeholder data (6 fake order cards, 3 fake reviews) alongside the real integrations.
- Several `console.log` calls log plaintext passwords and addresses (`LoginForm.jsx`,
  `RegisterForm.jsx`, `DeliveryAddressForm.jsx`).

## VCS

The git repo is rooted at **`Frontend/`**, not the repo root — there is one commit (CRA scaffold,
branch `master`) and the entire application is uncommitted. `Backend/` and the root
`package.json` are **not version controlled at all**. Run git commands from `Frontend/` and
expect a dirty tree before you change anything.

`commerce.zip` at the root is a stale copy of the original Spring Initializr seed; it has already
been unpacked into `Backend/commerce/`.
