package com.anchor.api.data.models.ozow;
/*
    🌼 🌼 Response Hash Check
        Follow these steps to validate the response:
        1. Concatenate the response variables (excluding Hash) in the order they appear in the response variables
        table
        2. Append your private key to the concatenated string. Your private key can be found in merchant details
        section of the merchant admin site.
        3. Convert the concatenated string to lowercase.
        4. Generate a SHA512 hash of the lowercase concatenated string.
        5. Compare generated hash to the Hash value in the response variables.
 */

public class OzowPaymentResponse {
    private String SiteCode, TransactionId, CurrencyCode, TransactionReference;
    private String Optional1, Optional2, Optional3, Optional4, Optional5;
    private boolean isTest;
    private double Amount;
    private String Status, StatusMessage, Hash;

    public String getSiteCode() {
        return SiteCode;
    }

    public void setSiteCode(String siteCode) {
        SiteCode = siteCode;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    public String getTransactionReference() {
        return TransactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        TransactionReference = transactionReference;
    }

    public String getOptional1() {
        return Optional1;
    }

    public void setOptional1(String optional1) {
        Optional1 = optional1;
    }

    public String getOptional2() {
        return Optional2;
    }

    public void setOptional2(String optional2) {
        Optional2 = optional2;
    }

    public String getOptional3() {
        return Optional3;
    }

    public void setOptional3(String optional3) {
        Optional3 = optional3;
    }

    public String getOptional4() {
        return Optional4;
    }

    public void setOptional4(String optional4) {
        Optional4 = optional4;
    }

    public String getOptional5() {
        return Optional5;
    }

    public void setOptional5(String optional5) {
        Optional5 = optional5;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        StatusMessage = statusMessage;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }
}
