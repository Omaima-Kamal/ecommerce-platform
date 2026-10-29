package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.AddItemRequest;
import com.oneteamplus.commerce.payload.response.ApiResponse;
import com.oneteamplus.commerce.service.CartService;
import com.oneteamplus.commerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Management", description = "find user cart, add item to cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        log.info("REST request to fetch shopping cart for user");

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating JWT for userId: {}", user.getId());

        Cart cart = cartService.findUserCart(user.getId());
        log.info("Successfully fetched cart with ID: {} for userId: {}", cart != null ? cart.getId() : "null", user.getId());

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestBody AddItemRequest req,
                                                      @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        log.info("REST request to add product ID: {} to user cart", req.getProductId());
        log.debug("Add to cart payload details - Quantity: {}, Size: {}", req.getQuantity(), req.getSize());

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating JWT for userId: {}", user.getId());

        cartService.addCartItem(user.getId(), req);
        log.info("Successfully added product ID: {} to cart for userId: {}", req.getProductId(), user.getId());

        ApiResponse res = new ApiResponse();
        res.setMessage("Item added to cart");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
