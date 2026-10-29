package com.oneteamplus.commerce.dto;

import java.util.List;

public class CallbackConfig {
    private List<String> successCallbackUrls;
    private List<String> failureCallbackUrls;
    private List<String> refundCallbackUrls;

    public CallbackConfig() {}

    public CallbackConfig(List<String> successCallbackUrls, List<String> failureCallbackUrls, List<String> refundCallbackUrls) {
        this.successCallbackUrls = successCallbackUrls;
        this.failureCallbackUrls = failureCallbackUrls;
        this.refundCallbackUrls = refundCallbackUrls;
    }

    public List<String> getSuccessCallbackUrls() { return successCallbackUrls; }
    public void setSuccessCallbackUrls(List<String> successCallbackUrls) { this.successCallbackUrls = successCallbackUrls; }

    public List<String> getFailureCallbackUrls() { return failureCallbackUrls; }
    public void setFailureCallbackUrls(List<String> failureCallbackUrls) { this.failureCallbackUrls = failureCallbackUrls; }

    public List<String> getRefundCallbackUrls() { return refundCallbackUrls; }
    public void setRefundCallbackUrls(List<String> refundCallbackUrls) { this.refundCallbackUrls = refundCallbackUrls; }
}