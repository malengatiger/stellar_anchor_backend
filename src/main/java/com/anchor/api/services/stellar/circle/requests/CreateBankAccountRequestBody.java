package com.anchor.api.services.stellar.circle.requests;


import com.anchor.api.services.stellar.circle.models.BankAddress;
import com.anchor.api.services.stellar.circle.models.BillingDetails;

public class CreateBankAccountRequestBody {
    private String idempotencyKey;
    private String beneficiaryName;
    private String accountNumber;
    private String routingNumber;
    private BillingDetails billingDetails;
    private BankAddress bankAddress;


    // Getter Methods

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public BillingDetails getBillingDetails() {
        return billingDetails;
    }

    public BankAddress getBankAddress() {
        return bankAddress;
    }

    // Setter Methods

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public void setBillingDetails(BillingDetails billingDetailsObject) {
        this.billingDetails = billingDetailsObject;
    }

    public void setBankAddress(BankAddress bankAddressObject) {
        this.bankAddress = bankAddressObject;
    }
}

