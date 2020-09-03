package com.anchor.api.data.models.ozow;

import java.math.BigDecimal;
import java.util.Date;

/*
Transaction Object
This is the object referred to in the response of the 2 API calls above.
*Please note the property name casing
Property Type Description
transactionId String (50) Ozow's unique reference for the transaction.
merchantCode String (50) Unique code assigned to each merchant.
siteCode String (50) Unique code assigned to each merchant site.
transactionReference String (50) Merchant's transaction reference.
currencyCode String (3) The transaction currency code.
amount Decimal (9,2) The transaction amount.
status String (50) The transaction status. Possible values are:
• Complete - The payment was successful
• Cancelled - The payment was cancelled
• Error - An error occurred while processing the payment
• Abandoned – The payment was abandoned
• PendingInvestigation – An inconclusive result was received by the bank
and the payment needs to be verified manually.
• Pending – The status cannot be determined as yet but will be reposted
to the notification URL as soon as it has been determined. Merchants
not using the notification URL will receive a PendingInvestigation
status.
statusMessage String (150) Message regarding the status of the transaction. This field will not always
have a value. This is a user friendly message that can be displayed to the
OZOW Integration Guide July 31, 2019
Page 11 of 14
user e.g. User cancelled transaction.
createdDate DateTime Transaction created date and time
paymentDate DateTime Transaction payment date and time
 */
public class OzowTransaction {
    private String transactionId, merchantCode, siteCode, transactionReference, currencyCode, status, statusMessage;
    private BigDecimal amount;
    private Date createdDate, paymentDate;

    public static final String
            COMPLETE = "Complete",
            CANCELLED = "Cancelled",
            ERROR = "Error",
            ABANDONED = "Abandoned",
            PENDING_INVESTIGATION = "PendingInvestigation";

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
