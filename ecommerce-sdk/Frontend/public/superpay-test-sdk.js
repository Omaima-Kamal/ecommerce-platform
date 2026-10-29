! function(e) {
    const SuperPay = {};
    SuperPay.initiateIframe = function(e = {}) {
        const {
            merchant: t,
            order: n,
            clientId: r,
            signature: o,
            redirectionURL: l,
            delayTime: i,
            defaultPaymentMode: s,
            merchantLanguage: d,
            callbackConfig: a,
            shipping: c,
            billing: y,
            merchantPaymentMethods: u,
            mode: p = "MODAL",
            embed: m = {},
            modalOptions: h = {},
            embedOptions: b = {}
        } = e, {
            code: f,
            apiKey: g
        } = t || {}, {
            merchantOrderId: w,
            amount: E,
            currency: x
        } = n || {};
        if (!(f && g && w && E && x && o)) throw new Error("SuperPay SDK: Missing required fields.");
        const {
            enabled: I = !1
        } = m, {
            fullscreen: v = !1,
            width: M,
            height: O
        } = h, {
            containerId: C,
            width: k,
            height: S,
            noScroll: $ = !1,
            styles: L = {}
        } = b, {
            container: P = {},
            wrapper: z = {},
            iframe: B = {}
        } = L, D = {
            merchant: {
                code: f,
                apiKey: g
            },
          
            order: {
                merchantOrderId: w,
                amount: parseFloat(E),
                currency: x
            },
            clientId: r || null,
            signature: o,
            redirectionURL: l || null,
            delayTime: i || null,
            defaultPaymentMode: s || null,
            merchantLanguage: d || null,
            callbackConfig: a || null,
            shipping: c || null,
            billing: y || null,
            merchantPaymentMethods: u || null,
            embed: I ? m : null
        };
        var T;
        console.log(D);
        const j = `https://merchant.super-pay.com/sp/pay/${T = JSON.stringify(D), btoa(T).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "")}`;
        if ("EMBED" === p) {
            if (!I) throw new Error("EMBED mode requires embed.enabled = true");
            if (!C) throw new Error("EMBED mode requires embedOptions.containerId");
            const e = document.getElementById(C);
            if (!e) throw new Error(`Container "${C}" not found`);
            Object.assign(e.style, P);
            const t = document.createElement("div");
            t.style.boxSizing = "border-box", t.style.width = k ? "number" == typeof k ? `${k}px` : k : "40%", Object.assign(t.style, z);
            const n = document.createElement("iframe");
            return n.src = j, n.style.border = "none", n.style.width = "100%", n.style.height = S ? "number" == typeof S ? `${S}px` : S : "700px", n.style.display = "block", n.style.overflow = "hidden", $ && n.setAttribute("scrolling", "no"), Object.assign(n.style, B), t.appendChild(n), e.innerHTML = "", void e.appendChild(t)
        }
        if (document.getElementById("superpay-overlay")) return;
        const q = document.createElement("div");
        q.id = "superpay-overlay", q.style.position = "fixed", q.style.inset = "0", q.style.background = "rgba(0,0,0,0.6)", q.style.zIndex = "9998";
        const K = document.createElement("iframe");
        K.src = j, K.style.position = "absolute", K.style.top = "50%", K.style.left = "50%", K.style.transform = "translate(-50%, -50%)", K.style.border = "none", K.style.borderRadius = "8px", K.style.zIndex = "9999", v ? (K.style.width = "100%", K.style.height = "100%") : M && O ? (K.style.width = "number" == typeof M ? `${M}px` : M, K.style.height = "number" == typeof O ? `${O}px` : O) : (K.style.width = "80%", K.style.height = "97%");
        const R = document.createElement("button");

        function A() {
            q.remove(), document.body.style.overflow = "auto"
        }
        R.innerText = "×", R.style.position = "absolute", R.style.top = "10px", R.style.right = "20px", R.style.fontSize = "32px", R.style.color = "#fff", R.style.background = "transparent", R.style.border = "none", R.style.cursor = "pointer", R.style.zIndex = "10000", R.onclick = A, q.onclick = e => e.target === q && A(), q.appendChild(K), q.appendChild(R), document.body.appendChild(q), document.body.style.overflow = "hidden"
    }, e.SuperPay = SuperPay
}(window);// This is just a sample script. Paste your real code (javascript or HTML) here.

if ('this_is'==/an_example/){of_beautifier();}else{var a=b?(c%d):e[f];}