package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.OrderException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Address;
import com.oneteamplus.commerce.model.Order;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.service.OrderService;
import com.oneteamplus.commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestBody Address shippingAddress,
                                             @RequestHeader("Authorization") String jwt) throws UserException {
        log.info("REST request to create new order");

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Creating order for userId: {}", user.getId());

        Order order = orderService.createOrder(user, shippingAddress);
        log.info("Successfully created order with ID: {} for userId: {}", order != null ? order.getId() : "null", user.getId());

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistory(
            @RequestHeader("Authorization") String jwt) throws UserException {
        log.info("REST request to fetch order history for authenticated user");

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Fetching order history for userId: {}", user.getId());

        List<Order> orders = orderService.userOrderHistory(user.getId());
        log.info("Retrieved {} orders for userId: {}", orders.size(), user.getId());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(
            @PathVariable("id") Long orderId,
            @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
        log.info("REST request to fetch order details for orderId: {}", orderId);

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating request for orderId: {} by userId: {}", orderId, user.getId());

        Order order = orderService.findOrderById(orderId);
        log.info("Successfully retrieved orderId: {} for userId: {}", orderId, user.getId());

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}