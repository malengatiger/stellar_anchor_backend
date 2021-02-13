package com.anchor.api.services.stellar.circle.models;


public class BankAccount {
    private String id;
    private String description;
    private String trackingRef;
    BillingDetails billingDetails;
    BankAddress bankAddress;
    private String createDate;
    private String updateDate;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTrackingRef() {
        return trackingRef;
    }

    public BillingDetails getBillingDetails() {
        return billingDetails;
    }

    public BankAddress getBankAddress() {
        return bankAddress;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public void setBillingDetails(BillingDetails billingDetailsObject) {
        this.billingDetails = billingDetailsObject;
    }

    public void setBankAddress(BankAddress bankAddressObject) {
        this.bankAddress = bankAddressObject;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}

