package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.CartItemException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.CartItem;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.response.ApiResponse;
import com.oneteamplus.commerce.service.CartItemService;
import com.oneteamplus.commerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart_items")
public class CartItemController {

    private static final Logger log = LoggerFactory.getLogger(CartItemController.class);

    private final CartItemService cartItemService;
    private final UserService userService;

    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @DeleteMapping("/{cartItemId}")
    @Operation(description = "Remove Cart Item From Cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "delete item")
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable Long cartItemId,
                                                      @RequestHeader("Authorization") String jwt) throws UserException, CartItemException {
        log.info("REST request to delete cart item ID: {}", cartItemId);

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Deleting cart item ID: {} for userId: {}", cartItemId, user.getId());

        cartItemService.removeCartItem(user.getId(), cartItemId);
        log.info("Successfully deleted cart item ID: {} for userId: {}", cartItemId, user.getId());

        ApiResponse res = new ApiResponse();
        res.setMessage("Item removed from cart");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{cartItemId}")
    @Operation(description = "Update Cart Item")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "update cart item")
    public ResponseEntity<CartItem> updateCartItem(@RequestBody CartItem req,
                                                      @PathVariable Long cartItemId,
                                                      @RequestHeader("Authorization") String jwt) throws UserException, CartItemException {
        log.info("REST request to update cart item ID: {}", cartItemId);
        log.debug("Update payload for cart item ID {}: Quantity = {}", cartItemId, req != null ? req.getQuantity() : "null");

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Updating cart item ID: {} for userId: {}", cartItemId, user.getId());

        CartItem updatedItem = cartItemService.updateCartItem(user.getId(), cartItemId, req);
        log.info("Successfully updated cart item ID: {} for userId: {}", cartItemId, user.getId());

        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }
}