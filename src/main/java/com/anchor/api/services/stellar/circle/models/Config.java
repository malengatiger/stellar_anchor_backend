package com.anchor.api.services.stellar.circle.models;


public class Config {
    Payments payments;


    // Getter Methods

    public Payments getPayments() {
        return payments;
    }

    // Setter Methods

    public void setPayments(Payments paymentsObject) {
        this.payments = paymentsObject;
    }
}
class Payments {
    private String masterWalletId;

    public String getMasterWalletId() {
        return masterWalletId;
    }

    public void setMasterWalletId(String masterWalletId) {
        this.masterWalletId = masterWalletId;
    }
}
