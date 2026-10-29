package com.oneteamplus.commerce.dto;


import java.math.BigDecimal;

public class OrderDetails {
    private String merchantOrderId;
    private BigDecimal amount;
    private String currency;

    public OrderDetails() {}

    public OrderDetails(String merchantOrderId, BigDecimal amount, String currency) {
        this.merchantOrderId = merchantOrderId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getMerchantOrderId() { return merchantOrderId; }
    public void setMerchantOrderId(String merchantOrderId) { this.merchantOrderId = merchantOrderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}