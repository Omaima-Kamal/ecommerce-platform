package com.oneteamplus.commerce.service;

import com.oneteamplus.commerce.model.Order;
import com.oneteamplus.commerce.model.User;
import com.oneteamplus.commerce.payload.response.PaymentConfigResponse;
import com.oneteamplus.commerce.payload.response.PaymentLinkResponse;

public interface PaymentService {
    PaymentLinkResponse createPaymentLink(Order order, User user);
    void scheduleCaptureAfterDelay(Order order, String authCode);
    public PaymentConfigResponse buildPaymentConfig(Order order, User user);
    }