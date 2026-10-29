package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.exception.ProductException;
import com.oneteamplus.commerce.model.Cart;
import com.oneteamplus.commerce.model.CartItem;
import com.oneteamplus.commerce.model.Product;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.AddItemRequest;
import com.oneteamplus.commerce.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImplementation implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImplementation.class);

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final ProductService productService;

    public CartServiceImplementation(CartRepository cartRepository, CartItemService cartItemService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        log.info("Created cart id={} for userId={}", savedCart.getId(), user.getId());
        return savedCart;
    }


    @Override
    @Transactional
    public String addCartItem(Long userId, AddItemRequest req)
            throws ProductException {
        log.info("Received add-to-cart request: productId={}, size={}, quantity={}",
                req.getProductId(),
                req.getSize(),
                req.getQuantity());
        if (req.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new IllegalStateException("Cart not found for user: " + userId);
        }

        Product product = productService.findProductById(req.getProductId());

        CartItem existing = cartItemService.isCartItemExist(
                cart, product, req.getSize(), userId);

        if (existing == null) {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setUserId(userId);
            item.setSize(req.getSize());
            item.setQuantity(req.getQuantity());
            item.setPrice(product.getPrice() * req.getQuantity());
            item.setDiscountedPrice(
                    product.getDiscountedPrice() * req.getQuantity());

            cart.addCartItem(item);
            cartItemService.createCartItem(item);

            log.info("Added cart item for userId={} productId={}",
                    userId, product.getId());
        } else {
            int newQuantity = existing.getQuantity() + req.getQuantity();

            existing.setQuantity(newQuantity);
            existing.setPrice(product.getPrice() * newQuantity);
            existing.setDiscountedPrice(
                    product.getDiscountedPrice() * newQuantity);

            cartItemService.updateCartItemQuantity(existing);

            log.info("Updated cart item id={} quantity={}",
                    existing.getId(), newQuantity);
        }

        return "Item added to cart";
    }

    @Override
    public Cart findUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice = totalPrice + cartItem.getPrice();
            totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
            totalItem = totalItem + cartItem.getQuantity();
        }

        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItems(totalItem);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalPrice - totalDiscountedPrice);

        log.info("Recomputed cart totals for userId={}: totalItems={} totalPrice={} totalDiscountedPrice={}",
                userId, totalItem, totalPrice, totalDiscountedPrice);

        return cartRepository.save(cart);
    }
}