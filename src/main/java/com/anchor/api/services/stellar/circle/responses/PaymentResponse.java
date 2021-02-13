package com.anchor.api.services.stellar.circle.responses;

import com.anchor.api.services.stellar.circle.models.Amount;

public class PaymentResponse {
    private String trackingRef;
    Amount AmountObject;
    private String status;


    // Getter Methods

    public String getTrackingRef() {
        return trackingRef;
    }

    public Amount getAmount() {
        return AmountObject;
    }

    public String getStatus() {
        return status;
    }

    // Setter Methods

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public void setAmount(Amount amountObject) {
        this.AmountObject = amountObject;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
