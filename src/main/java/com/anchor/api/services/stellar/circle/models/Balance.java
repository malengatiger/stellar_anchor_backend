package com.anchor.api.services.stellar.circle.models;

public class Balance {
    private String amount;
    private String currency;

    // Getter Methods

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    // Setter Methods

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
