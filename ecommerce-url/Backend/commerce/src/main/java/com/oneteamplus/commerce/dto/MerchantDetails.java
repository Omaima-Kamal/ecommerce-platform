package com.oneteamplus.commerce.dto;

public class MerchantDetails {
    private String code;
    private String apiKey;

    public MerchantDetails() {}

    public MerchantDetails(String code, String apiKey) {
        this.code = code;
        this.apiKey = apiKey;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}