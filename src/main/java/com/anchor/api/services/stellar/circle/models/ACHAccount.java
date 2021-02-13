package com.anchor.api.services.stellar.circle.models;

public class ACHAccount {
    private String accountNumber;
    private String routingNumber;
    private String description;


    // Getter Methods

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public String getDescription() {
        return description;
    }

    // Setter Methods

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
