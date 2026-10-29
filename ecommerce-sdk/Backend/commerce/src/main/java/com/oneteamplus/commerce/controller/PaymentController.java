package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.exception.OrderException;
import com.oneteamplus.commerce.exception.UserException;
import com.oneteamplus.commerce.model.Order;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.response.PaymentConfigResponse;
import com.oneteamplus.commerce.payload.response.PaymentLinkResponse;
import com.oneteamplus.commerce.repository.OrderRepository;
import com.oneteamplus.commerce.service.OrderService;
import com.oneteamplus.commerce.service.PaymentService;
import com.oneteamplus.commerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final OrderService orderService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public PaymentController(OrderService orderService, UserService userService,
                             OrderRepository orderRepository, PaymentService paymentService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt
    ) throws UserException, OrderException {
        log.info("REST request to create payment link for order ID: {}", orderId);

        User user = userService.findUserProfileByJwt(jwt);
        log.debug("Authenticating payment creation request for userId: {}", user.getId());

        Order order = orderService.findOrderById(orderId);

        PaymentLinkResponse response = paymentService.createPaymentLink(order, user);
        log.info("Successfully generated payment link for order ID: {} and userId: {}", orderId, user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/config")
    public ResponseEntity<PaymentConfigResponse> getPaymentConfig(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt
    ) throws UserException, OrderException {
        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.findOrderById(orderId);
        PaymentConfigResponse config = paymentService.buildPaymentConfig(order, user);
        return new ResponseEntity<>(config, HttpStatus.OK);
    }
}