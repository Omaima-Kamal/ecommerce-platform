package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.CartItemException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.CartItem;
import com.oneteamplus.commerce.model.Product;

public interface CartItemService {

    public CartItem createCartItem(CartItem cartItem);

    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException;

    public CartItem isCartItemExist(Cart cart, Product product,String size, Long userId);

    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException;

    public CartItem findCartItemById(Long cartItemId) throws CartItemException;

    public String emptyUserCart(Long userId);

    CartItem updateCartItemQuantity(CartItem cartItem);
}
