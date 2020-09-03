package com.anchor.api.data.models.payfast;

public class PayFastRequest {
    private String merchant_id, merchant_key, return_url,
    cancel_url, notify_url, signature;
    private PayFastTransactionDetails transactionDetails;
    private PayFastBuyerDetails buyerDetails;

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_key() {
        return merchant_key;
    }

    public void setMerchant_key(String merchant_key) {
        this.merchant_key = merchant_key;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getCancel_url() {
        return cancel_url;
    }

    public void setCancel_url(String cancel_url) {
        this.cancel_url = cancel_url;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public PayFastTransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(PayFastTransactionDetails transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public PayFastBuyerDetails getBuyerDetails() {
        return buyerDetails;
    }

    public void setBuyerDetails(PayFastBuyerDetails buyerDetails) {
        this.buyerDetails = buyerDetails;
    }
}
