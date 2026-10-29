package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.CartItemException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.CartItem;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.repository.CartItemRepository;
import com.oneteamplus.commerce.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartItemServiceImplementation implements CartItemService {

    private static final Logger log = LoggerFactory.getLogger(CartItemServiceImplementation.class);

    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final CartRepository cartRepository;

    public CartItemServiceImplementation(CartItemRepository cartItemRepository, UserService userService, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public CartItem createCartItem(CartItem cartItem) {
        CartItem createdCartItem = cartItemRepository.save(cartItem);

        log.info("Created cart item id={} userId={} productId={}",
                createdCartItem.getId(),
                createdCartItem.getUserId(),
                createdCartItem.getProduct().getId());

        return createdCartItem;
    }

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {
        log.info("Updating cart item id={} requested by userId={}", id, userId);

        CartItem item = findCartItemById(id);
        User user = userService.findUserById(item.getUserId());

        if (user.getId().equals(userId)) {
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(item.getQuantity() * item.getProduct().getPrice());
            item.setDiscountedPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
        } else {
            log.warn("userId={} attempted to update cart item id={} owned by userId={}",
                    userId, id, item.getUserId());
        }

        return cartItemRepository.save(item);
    }

    @Override
    public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {
        return cartItemRepository.isCartItemExist(cart, product, size, userId);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId)
            throws CartItemException, UserException {

        CartItem cartItem = findCartItemById(cartItemId);

        if (!userId.equals(cartItem.getUserId())) {
            log.warn(
                    "userId={} attempted to remove cart item id={} owned by userId={}",
                    userId, cartItemId, cartItem.getUserId()
            );

            throw new UserException("You cannot remove another user's item");
        }

        Cart cart = cartItem.getCart();

        cart.removeCartItem(cartItem);

        log.info(
                "Removed cart item id={} for userId={}",
                cartItemId, userId
        );
    }

    @Override
    public CartItem findCartItemById(Long cartItemId) throws CartItemException {
        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

        if (opt.isPresent()) {
            return opt.get();
        }
        log.warn("Cart item not found id={}", cartItemId);
        throw new CartItemException("Cart item not found with id : " + cartItemId);
    }

    @Override
    public String emptyUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        int deleted = cartItemRepository.deleteByCartUserId(userId);
        log.info("Emptied cart for userId={}, cartId={}, itemsDeleted={}",
                userId, cart != null ? cart.getId() : null, deleted);
        return "User Cart is empty";
    }

    @Override
    public CartItem updateCartItemQuantity(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
}