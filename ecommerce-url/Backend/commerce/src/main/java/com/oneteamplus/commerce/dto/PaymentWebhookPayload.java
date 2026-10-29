package com.oneteamplus.commerce.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PaymentWebhookPayload {
    @JsonAlias("transactionStatus")
    private String status;
    private String authCode;
    private String scenarioId;
    private String orderId;
    private String declineCode;
    private String declineMessage;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }
    public String getDeclineCode() { return declineCode; }
    public void setDeclineCode(String declineCode) { this.declineCode = declineCode; }
    public String getDeclineMessage() { return declineMessage; }
    public void setDeclineMessage(String declineMessage) { this.declineMessage = declineMessage; }
    public String getOrderId() {return orderId;}
    public void setOrderId(String orderId) {this.orderId = orderId;}
}