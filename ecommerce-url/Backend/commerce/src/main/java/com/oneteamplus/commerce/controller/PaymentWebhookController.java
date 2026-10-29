package com.oneteamplus.commerce.controller;

import com.oneteamplus.commerce.dto.PaymentWebhookPayload;
import com.oneteamplus.commerce.enums.PaymentStatus;
import com.oneteamplus.commerce.service.PaymentWebhookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook")
public class PaymentWebhookController {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentWebhookController.class);

    private final PaymentWebhookService paymentWebhookService;
    private final ObjectMapper objectMapper;

    public PaymentWebhookController(
            PaymentWebhookService paymentWebhookService,
            ObjectMapper objectMapper) {
        this.paymentWebhookService = paymentWebhookService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/success")
    public ResponseEntity<Void> handleSuccess(
            @RequestBody JsonNode rawBody) {

        processWebhook(rawBody, PaymentStatus.COMPLETED, "PLACED");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/failure")
    public ResponseEntity<Void> handleFailure(
            @RequestBody JsonNode rawBody) {

        processWebhook(rawBody, PaymentStatus.FAILED, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> handleRefund(
            @RequestBody JsonNode rawBody) {

        processWebhook(rawBody, PaymentStatus.REFUNDED, null);
        return ResponseEntity.ok().build();
    }

    private void processWebhook(
            JsonNode rawBody,
            PaymentStatus defaultStatus,
            String orderStatus) {

        if (rawBody == null || !rawBody.isObject()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid payment webhook body");
        }

        try {
            PaymentWebhookPayload payload =
                    objectMapper.treeToValue(
                            rawBody,
                            PaymentWebhookPayload.class);

            Map<String, Object> fullResponse =
                    objectMapper.convertValue(
                            rawBody,
                            new TypeReference<Map<String, Object>>() {});

            String orderId =
                    payload != null ? payload.getOrderId() : null;

            log.info(
                    "Received payment webhook: orderId={}, defaultStatus={}",
                    orderId,
                    defaultStatus);

            paymentWebhookService.applyStatus(
                    payload,
                    defaultStatus,
                    orderStatus,
                    fullResponse);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Could not process payment webhook JSON", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid payment webhook payload",
                    e);
        }
    }
}