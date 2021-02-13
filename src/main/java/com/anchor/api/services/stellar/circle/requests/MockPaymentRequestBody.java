package com.anchor.api.services.stellar.circle.requests;


import com.anchor.api.services.stellar.circle.models.Amount;

public class MockPaymentRequestBody {
    private String trackingRef;
    private Amount amount;
    public String getTrackingRef() {
        return trackingRef;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public void setAmount(Amount amountObject) {
        this.amount = amountObject;
    }
}

