package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.enums.OrderStatus;
import com.oneteamplus.commerce.enums.PaymentStatus;
import com.oneteamplus.commerce.exception.OrderException;
import com.oneteamplus.commerce.model.*;
import com.oneteamplus.commerce.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImplementation implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImplementation.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImplementation(OrderRepository orderRepository, CartService cartService, AddressRepository addressRepository, UserRepository userRepository, OrderItemService orderItemService, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public Order createOrder(User user, Address shippingAddress) {
        log.info("Creating order for userId={}", user.getId());

        Address address = new Address();
        address.setFirstName(shippingAddress.getFirstName());
        address.setLastName(shippingAddress.getLastName());
        address.setStreetAddress(shippingAddress.getStreetAddress());
        address.setCity(shippingAddress.getCity());
        address.setState(shippingAddress.getState());
        address.setZipCode(shippingAddress.getZipCode());
        address.setMobile(shippingAddress.getMobile());
        address.setUser(user);
        address = addressRepository.save(address);
        user.getAddress().add(address);
        userRepository.save(user);

        Cart cart = cartService.findUserCart(user.getId());

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(cart.getTotalPrice());
        order.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
        order.setDiscount(cart.getDiscount());
        order.setTotalItems(cart.getTotalItems());
        order.setShippingAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING.getValue());
        order.getPaymentDetails().setStatus(PaymentStatus.PENDING.getValue());
        order.setCreatedAt(LocalDateTime.now());

        String generatedOrderId =
                "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        order.setOrderId(generatedOrderId);
        log.info("Generated orderId={}", generatedOrderId);

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSize(cartItem.getSize());
            orderItem.setUserId(cartItem.getUserId());
            orderItem.setDiscountedPrice(cartItem.getDiscountedPrice());

            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        log.info("Created order id={} for userId={}: {} items, totalPrice={}, totalDiscountedPrice={}",
                savedOrder.getId(), user.getId(), savedOrder.getOrderItems().size(),
                savedOrder.getTotalPrice(), savedOrder.getTotalDiscountedPrice());

        return savedOrder;
    }

    @Override
    public Order placedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.PLACED.getValue());
        order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED.getValue());
        Order saved = orderRepository.save(order);
        log.info("Order id={} marked PLACED, payment COMPLETED", orderId);
        return saved;
    }

    @Override
    public Order confirmedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.CONFIRMED.getValue());
        Order saved = orderRepository.save(order);
        log.info("Order id={} marked CONFIRMED", orderId);
        return saved;
    }

    @Override
    public Order shippedOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.SHIPPED.getValue());
        Order saved = orderRepository.save(order);
        log.info("Order id={} marked SHIPPED", orderId);
        return saved;
    }

    @Override
    public Order deliveredOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.DELIVERED.getValue());
        Order saved = orderRepository.save(order);
        log.info("Order id={} marked DELIVERED", orderId);
        return saved;
    }

    @Override
    public Order cancelledOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus(OrderStatus.CANCELLED.getValue());
        Order saved = orderRepository.save(order);
        log.info("Order id={} marked CANCELLED", orderId);
        return saved;
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {
        Optional<Order> opt = orderRepository.findById(orderId);

        if (opt.isPresent()) {
            return opt.get();
        }
        log.warn("Order not found id={}", orderId);
        throw new OrderException("order not exist with id : " + orderId);
    }

    @Override
    public List<Order> userOrderHistory(Long userId) {
        List<Order> orders = orderRepository.getUsersOrders(userId);
        log.info("Fetched {} orders for userId={}", orders.size(), userId);
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
        log.info("Deleted order id={}", orderId);
    }
}