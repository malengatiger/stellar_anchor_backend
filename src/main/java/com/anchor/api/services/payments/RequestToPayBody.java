package com.anchor.api.services.payments;


public class RequestToPayBody {
    private String amount;
    private String currency;
    private String externalId;
    private Payer payer;
    private String payerMessage;
    private String payeeNote;


    // Getter Methods

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getExternalId() {
        return externalId;
    }

    public Payer getPayer() {
        return payer;
    }

    public String getPayerMessage() {
        return payerMessage;
    }

    public String getPayeeNote() {
        return payeeNote;
    }

    // Setter Methods

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setPayer(Payer payerObject) {
        this.payer = payerObject;
    }

    public void setPayerMessage(String payerMessage) {
        this.payerMessage = payerMessage;
    }

    public void setPayeeNote(String payeeNote) {
        this.payeeNote = payeeNote;
    }
}
