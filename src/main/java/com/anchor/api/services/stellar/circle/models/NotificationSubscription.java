package com.anchor.api.services.stellar.circle.models;


import java.util.ArrayList;
import java.util.List;

public class NotificationSubscription {
    private String id;
    private String endpoint;
    List< SubscriptionDetails > subscriptionDetails = new ArrayList< SubscriptionDetails >();

    public List<SubscriptionDetails> getSubscriptionDetails() {
        return subscriptionDetails;
    }

    public void setSubscriptionDetails(List<SubscriptionDetails> subscriptionDetails) {
        this.subscriptionDetails = subscriptionDetails;
    }

// Getter Methods

    public String getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
