package com.anchor.api.services.stellar.circle.requests;


public class GenerateBlockchainAddressBody {
    private String idempotencyKey;
    private String currency;
    private String chain;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getCurrency() {
        return currency;
    }

    public String getChain() {
        return chain;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }
}
