package com.anchor.api.services.stellar.circle.models;


public class SubscriptionDetails {
    private String url;
    private String status;

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
