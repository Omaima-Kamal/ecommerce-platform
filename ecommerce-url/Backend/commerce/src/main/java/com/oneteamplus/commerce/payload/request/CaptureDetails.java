package com.oneteamplus.commerce.payload.request;

public class CaptureDetails implements java.io.Serializable {
    private String authCode;
    private Long captureAmount;
    private String currency;
    private String orderId;
    private String description;

    public CaptureDetails() {}

    public CaptureDetails(String authCode, Long captureAmount, String currency, String orderId, String description) {
        this.authCode = authCode;
        this.captureAmount = captureAmount;
        this.currency = currency;
        this.orderId = orderId;
        this.description = description;
    }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
    public Long getCaptureAmount() { return captureAmount; }
    public void setCaptureAmount(Long captureAmount) { this.captureAmount = captureAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}