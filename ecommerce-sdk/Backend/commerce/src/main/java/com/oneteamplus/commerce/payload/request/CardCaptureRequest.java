package com.oneteamplus.commerce.payload.request;

import java.io.Serializable;

public class CardCaptureRequest implements Serializable {
    private Long merchantId;
    private String merchantApiKey;
    private CaptureDetails captureDetails;

    public CardCaptureRequest() {}

    public CardCaptureRequest(Long merchantId, String merchantApiKey, CaptureDetails captureDetails) {
        this.merchantId = merchantId;
        this.merchantApiKey = merchantApiKey;
        this.captureDetails = captureDetails;
    }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public String getMerchantApiKey() { return merchantApiKey; }
    public void setMerchantApiKey(String merchantApiKey) { this.merchantApiKey = merchantApiKey; }
    public CaptureDetails getCaptureDetails() { return captureDetails; }
    public void setCaptureDetails(CaptureDetails captureDetails) { this.captureDetails = captureDetails; }
}