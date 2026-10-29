package com.oneteamplus.commerce.payload.request;


import com.oneteamplus.commerce.dto.CallbackConfig;
import com.oneteamplus.commerce.dto.CustomerDetails;
import com.oneteamplus.commerce.dto.MerchantDetails;
import com.oneteamplus.commerce.dto.OrderDetails;

public class PaymentLinkRequest {
    private MerchantDetails merchant;
    private OrderDetails order;
    private String signature;
    private CustomerDetails customer;
    private String redirectionURL;
    private Integer delayTime;
    private String defaultPaymentMode;
    private String captureType;
    private String merchantLanguage;
    private CallbackConfig callbackConfig;

    public MerchantDetails getMerchant() { return merchant; }
    public void setMerchant(MerchantDetails merchant) { this.merchant = merchant; }

    public OrderDetails getOrder() { return order; }
    public void setOrder(OrderDetails order) { this.order = order; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public CustomerDetails getCustomer() { return customer; }
    public void setCustomer(CustomerDetails customer) { this.customer = customer; }

    public String getRedirectionURL() { return redirectionURL; }
    public void setRedirectionURL(String redirectionURL) { this.redirectionURL = redirectionURL; }

    public Integer getDelayTime() { return delayTime; }
    public void setDelayTime(Integer delayTime) { this.delayTime = delayTime; }

    public String getDefaultPaymentMode() { return defaultPaymentMode; }
    public void setDefaultPaymentMode(String defaultPaymentMode) { this.defaultPaymentMode = defaultPaymentMode; }

    public String getMerchantLanguage() { return merchantLanguage; }
    public void setMerchantLanguage(String merchantLanguage) { this.merchantLanguage = merchantLanguage; }

    public CallbackConfig getCallbackConfig() { return callbackConfig; }
    public void setCallbackConfig(CallbackConfig callbackConfig) { this.callbackConfig = callbackConfig; }

    public String getCaptureType() {
        return captureType;
    }

    public void setCaptureType(String captureType) {
        this.captureType = captureType;
    }
}