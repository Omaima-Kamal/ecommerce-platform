package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.dto.PaymentWebhookPayload;
import com.oneteamplus.commerce.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oneteamplus.commerce.model.Order;
import com.oneteamplus.commerce.model.PaymentDetails;
import com.oneteamplus.commerce.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
public class PaymentWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);

    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;
    private final PaymentService paymentService;

    @Value("${capture.type}")
    private String captureType;

    public PaymentWebhookService(
            OrderRepository orderRepository,
            CartItemService cartItemService,
            PaymentService paymentService) {

        this.orderRepository = orderRepository;
        this.cartItemService = cartItemService;
        this.paymentService = paymentService;
    }

    @Transactional
    public void applyStatus(
            PaymentWebhookPayload payload,
            PaymentStatus defaultStatus,
            String orderStatus,
            Map<String, Object> fullTransactionResponse) {

        if (payload == null || payload.getOrderId() == null) {
            log.warn("Payment webhook received with null payload or missing orderId");
            return;
        }

        String merchantOrderId = payload.getOrderId();
        Order order = orderRepository.findByOrderId(merchantOrderId);

        if (order == null) {
            log.warn("No order found for orderId={}", merchantOrderId);
            return;
        }

        PaymentStatus targetStatus =
                resolvePaymentStatus(payload.getStatus(), defaultStatus);

        PaymentDetails paymentDetails = order.getPaymentDetails();

        if (paymentDetails == null) {
            paymentDetails = new PaymentDetails();
            order.setPaymentDetails(paymentDetails);
        }

        boolean alreadyCompleted =
                PaymentStatus.COMPLETED.getValue()
                        .equalsIgnoreCase(paymentDetails.getStatus());

        paymentDetails.setStatus(targetStatus.getValue());
        paymentDetails.setPaymentTrxDetails(fullTransactionResponse);

        if (fullTransactionResponse != null) {
            Object transactionIdValue =
                    fullTransactionResponse.get("transactionId");

            if (transactionIdValue != null
                    && !transactionIdValue.toString().isBlank()) {
                paymentDetails.setPaymentId(transactionIdValue.toString());
            }
        }

        order.setPaymentDetails(paymentDetails);

        if (orderStatus != null) {
            order.setOrderStatus(orderStatus);
        }

        orderRepository.saveAndFlush(order);

        log.info(
                "Payment webhook applied: orderId={}, paymentStatus={}",
                merchantOrderId,
                targetStatus
        );

        if (targetStatus == PaymentStatus.COMPLETED) {
            if ("AUTH_ONLY".equals(captureType) && !alreadyCompleted) {
                paymentService.scheduleCaptureAfterDelay(
                        order,
                        payload.getAuthCode()
                );
            }

            cartItemService.emptyUserCart(order.getUser().getId());
        }
    }

    private PaymentStatus resolvePaymentStatus(String rawStatus, PaymentStatus fallback) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return fallback;
        }

        String normalized = rawStatus.trim().toUpperCase();

        switch (normalized) {
            case "DECLINED":
            case "DECLINE":
                return PaymentStatus.DECLINED;
            case "FAILED":
            case "FAILURE":
                return PaymentStatus.FAILED;
            case "COMPLETED":
            case "SUCCESS":
            case "APPROVED":
            case "AUTHORIZED":
            case "VOIDED":
            case "CAPTURED":
                return PaymentStatus.COMPLETED;
            default:
                try {
                    return PaymentStatus.valueOf(normalized);
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown payload status '{}', falling back to {}", rawStatus, fallback);
                    return fallback;
                }
        }
    }
}