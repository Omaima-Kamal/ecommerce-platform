package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.dto.CallbackConfig;
import com.oneteamplus.commerce.dto.CustomerDetails;
import com.oneteamplus.commerce.dto.MerchantDetails;
import com.oneteamplus.commerce.dto.OrderDetails;
import com.oneteamplus.commerce.model.Address;
import com.oneteamplus.commerce.model.Order;
import com.oneteamplus.commerce.model.PaymentDetails;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.request.CaptureDetails;
import com.oneteamplus.commerce.payload.request.CardCaptureRequest;
import com.oneteamplus.commerce.payload.request.PaymentLinkRequest;
import com.oneteamplus.commerce.payload.response.PaymentConfigResponse;
import com.oneteamplus.commerce.payload.response.PaymentLinkResponse;
import com.oneteamplus.commerce.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PaymentServiceImplementation implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImplementation.class);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @Value("${superpay.merchant.code}")
    private String merchantCode;

    @Value("${superpay.merchant.apiKey}")
    private String apiKey;

    @Value("${superpay.merchant.secret}")
    private String merchantSecret;

    @Value("${superpay.gateway.url}")
    private String gatewayUrl;

    @Value("${app.redirect.url}")
    private String redirectUrl;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${capture.type}")
    private String captureType;

    @Value("${capture.delay.seconds}")
    private long captureDelaySeconds;

    @Value("${superpay.gateway.capture-url}")
    private String captureUrl;

    public PaymentServiceImplementation(RestTemplate restTemplate, ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentLinkResponse createPaymentLink(Order order, User user) {
        Address shipping = order.getShippingAddress();
        String merchantOrderId = order.getOrderId();
        String currency = "EGP";
        String customerMobile = shipping.getMobile();

        BigDecimal amount = BigDecimal.valueOf(order.getTotalDiscountedPrice());
        log.info("Creating payment link: orderId={} merchantOrderId={} amount={} currency={}",
                order.getId(), merchantOrderId, amount, currency);

        String signature = generateSignature(merchantCode, customerMobile, amount, currency, merchantOrderId);

        PaymentLinkRequest request = new PaymentLinkRequest();
        request.setMerchant(new MerchantDetails(merchantCode, apiKey));
        request.setOrder(new OrderDetails(merchantOrderId, amount, currency));
        request.setSignature(signature);
        request.setCustomer(new CustomerDetails(
                shipping.getFirstName() + " " + shipping.getLastName(),
                null,
                customerMobile));
        request.setRedirectionURL(redirectUrl + "/payment/" + order.getId());
        request.setDelayTime(900);
        request.setDefaultPaymentMode("AUTH_AND_CAPTURE");
        request.setMerchantLanguage("EN");
        request.setCallbackConfig(new CallbackConfig(
                List.of(backendUrl + "/api/payments/webhook/success"),
                List.of(backendUrl + "/api/payments/webhook/failure"),
                List.of(backendUrl + "/api/payments/webhook/refund")));
        request.setCaptureType(captureType);

        String encodedBody;
        try {
            encodedBody = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(request));
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize payment request", e);
        }

        URI uri = UriComponentsBuilder
                .fromUriString(gatewayUrl)
                .pathSegment(encodedBody)
                .queryParam("idempotencyKey", UUID.randomUUID().toString())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.POST, new HttpEntity<>(headers), String.class);

            URI location = response.getHeaders().getLocation();
            if (location == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Gateway returned " + response.getStatusCode() + " without a Location header");
            }

            String path = location.getPath();
            String sessionId = path.substring(path.lastIndexOf('/') + 1);

            Map<String, Object> gatewayResponse = new HashMap<>();
            String bodyText = response.getBody();

            if (bodyText != null && !bodyText.isBlank()) {
                try {
                    gatewayResponse = objectMapper.readValue(
                            bodyText,
                            new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});
                } catch (Exception e) {
                    log.warn("Gateway response body was not JSON for orderId={}", merchantOrderId);
                }
            }

            PaymentDetails paymentDetails = order.getPaymentDetails();
            if (paymentDetails == null) {
                paymentDetails = new PaymentDetails();
                order.setPaymentDetails(paymentDetails);
            }

            if (!gatewayResponse.isEmpty()) {
                paymentDetails.setPaymentTrxDetails(gatewayResponse);
            }

            Object trxIdValue = gatewayResponse.get("transactionId");
            if (trxIdValue == null || trxIdValue.toString().isBlank()) {
                trxIdValue = gatewayResponse.get("trxId");
            }
            if (trxIdValue == null || trxIdValue.toString().isBlank()) {
                trxIdValue = response.getHeaders().getFirst("X-Transaction-Id");
            }
            String transactionId = trxIdValue == null ? null : trxIdValue.toString().trim();

            if (transactionId != null && !transactionId.isEmpty()) {
                paymentDetails.setPaymentId(transactionId);
            } else {
                log.info("No transaction id in the creation response for orderId={}; it will be saved from the webhook",
                        merchantOrderId);
            }

            orderRepository.saveAndFlush(order);

            PaymentLinkResponse result = new PaymentLinkResponse();
            result.setPaymentUrl(location.toString());
            result.setSessionId(sessionId);
            result.setStatus("REDIRECT");

            return result;

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            log.error("Payment gateway unreachable", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Payment gateway unreachable", e);
        }
    }

    /**
     * Matches ExternalPaymentService.buildSignatureString() + verifySignature() exactly:
     * HMAC-SHA256(merchantId + customerMobile + amount + currency + orderId, secretKey), hex-encoded.
     */
    private String generateSignature(
            String merchantId,
            String customerMobile,
            BigDecimal amount,
            String currency,
            String orderId) {

        try {
            //            String signatureData = orderId + amount+ ".00" + currency;
            String signatureData = merchantId
                    + customerMobile
                    + amount
                    + currency
                    + orderId;

            log.info("Signature data: [{}]", signatureData);

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(
                    merchantSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            ));

            byte[] hmacBytes = mac.doFinal(
                    signatureData.getBytes(StandardCharsets.UTF_8)
            );

            return toHex(hmacBytes);

        } catch (Exception e) {

            log.error("Failed to generate payment signature for orderId={}", orderId, e);
            throw new RuntimeException("Failed to generate payment signature", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    @Async
    public void scheduleCaptureAfterDelay(Order order, String authCode) {
        try {
            Thread.sleep(captureDelaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Capture delay interrupted for orderId={}", order.getId(), e);
            return;
        }
        sendCaptureRequest(order, authCode);
    }

    private void sendCaptureRequest(Order order, String authCode) {
        CaptureDetails captureDetails = new CaptureDetails(
                authCode,
                (long) order.getTotalDiscountedPrice(),
                "EGP",
                order.getOrderId(),
                "Capture for order " + order.getOrderId()
        );

        CardCaptureRequest request = new CardCaptureRequest(
                Long.valueOf(merchantCode),
                apiKey,
                captureDetails
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("channel", "SANDBOX-PORTAL");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    captureUrl, new HttpEntity<>(request, headers), String.class);

            log.info("Capture request sent for orderId={} authCode={} -> status={}",
                    order.getOrderId(), authCode, response.getStatusCode());

        } catch (HttpClientErrorException e) {
            log.error("Capture request rejected for orderId={}: status={} body={}",
                    order.getOrderId(), e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Capture request failed for orderId={}", order.getOrderId(), e);
        }
    }

    @Override
    public PaymentConfigResponse buildPaymentConfig(Order order, User user) {
        Address shipping = order.getShippingAddress();
        String merchantOrderId = order.getOrderId();
        String currency = "EGP";
        String customerMobile = shipping.getMobile();
        double amountValue = order.getTotalDiscountedPrice();

        BigDecimal amount = BigDecimal.valueOf(order.getTotalDiscountedPrice());

        log.info("Building payment config (iframe): orderId={} merchantOrderId={} amount={}",
                order.getId(), merchantOrderId, amountValue);

        String signature = generateSignature(merchantCode, customerMobile, amount, currency, merchantOrderId);

        PaymentConfigResponse config = new PaymentConfigResponse();
        config.setMerchant(new MerchantDetails(merchantCode, apiKey));
        config.setOrder(new OrderDetails(merchantOrderId, amount, currency));
        config.setSignature(signature);
        config.setCustomer(new CustomerDetails(
                shipping.getFirstName() + " " + shipping.getLastName(),
                user.getEmail(),
                customerMobile));
        config.setRedirectionURL(redirectUrl + "/payment/" + order.getId());
        config.setDelayTime(900);
        config.setDefaultPaymentMode("AUTH_AND_CAP");
        config.setMerchantLanguage("EN");
        config.setCallbackConfig(new CallbackConfig(
                List.of(backendUrl + "/api/payments/webhook/success"),
                List.of(backendUrl + "/api/payments/webhook/failure"),
                List.of(backendUrl + "/api/payments/webhook/refund")));
        config.setCaptureType(captureType);

        return config;
    }
}