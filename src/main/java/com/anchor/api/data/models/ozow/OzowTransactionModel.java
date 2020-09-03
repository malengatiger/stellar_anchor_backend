package com.anchor.api.data.models.ozow;

import java.math.BigDecimal;
import java.util.Date;

/*
TransactionModel Object
This is the object referred to in the response of the calls above.
*Please note the property name casing
Property Type Description
id String (50) Ozow's unique reference for the transaction.
createdDateUtc DateTime Date the transaction was created.
siteCode String (50) Unique code assigned to each merchant site.
siteName String (50) Merchant site name.
bankFromName String (50) The bank the payment has been made from.
bankToName String (50) The bank the payment has been made to.
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
toAccount String (50) The account number.
toReference String (20) The bank reference that was used to make the payment.
paymentDate DateTime Date the payment was successfully completed.
paymentDateUtc DateTime UTC date the payment was successfully completed.
transactionReference String (50) The merchant’s reference for the transaction.
customer String (50) Customer’s name
optional1 String (50) Optional 1 value received in the original transaction request.
optional2 String (50) Optional 2 value received in the original transaction request.
optional3 String (50) Optional 3 value received in the original transaction request.
optional4 String (50) Optional 4 value received in the original transaction request.
optional5 String (50) Optional 5 value received in the original transaction request.
lastEvent String (50) Used for administration purposes, will be empty for most merchants.
lastEventStatus String (50) Used for administration purposes, will be empty for most merchants.
 */
public class OzowTransactionModel {
    private String id, siteCode, siteName, bankFromName, bankToName, status, toAccount, toReference, transactionReference;
    private String optional1, optional2, optional3, optional4, optional5, lastEvent, lastEventStatus;
    private Date createdDateUtc, paymentDate, paymentDateUtc ;
    private BigDecimal amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBankFromName() {
        return bankFromName;
    }

    public void setBankFromName(String bankFromName) {
        this.bankFromName = bankFromName;
    }

    public String getBankToName() {
        return bankToName;
    }

    public void setBankToName(String bankToName) {
        this.bankToName = bankToName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getToReference() {
        return toReference;
    }

    public void setToReference(String toReference) {
        this.toReference = toReference;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getOptional1() {
        return optional1;
    }

    public void setOptional1(String optional1) {
        this.optional1 = optional1;
    }

    public String getOptional2() {
        return optional2;
    }

    public void setOptional2(String optional2) {
        this.optional2 = optional2;
    }

    public String getOptional3() {
        return optional3;
    }

    public void setOptional3(String optional3) {
        this.optional3 = optional3;
    }

    public String getOptional4() {
        return optional4;
    }

    public void setOptional4(String optional4) {
        this.optional4 = optional4;
    }

    public String getOptional5() {
        return optional5;
    }

    public void setOptional5(String optional5) {
        this.optional5 = optional5;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getLastEventStatus() {
        return lastEventStatus;
    }

    public void setLastEventStatus(String lastEventStatus) {
        this.lastEventStatus = lastEventStatus;
    }

    public Date getCreatedDateUtc() {
        return createdDateUtc;
    }

    public void setCreatedDateUtc(Date createdDateUtc) {
        this.createdDateUtc = createdDateUtc;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getPaymentDateUtc() {
        return paymentDateUtc;
    }

    public void setPaymentDateUtc(Date paymentDateUtc) {
        this.paymentDateUtc = paymentDateUtc;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
