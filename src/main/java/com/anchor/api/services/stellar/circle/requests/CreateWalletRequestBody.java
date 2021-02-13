package com.anchor.api.services.stellar.circle.requests;


public class CreateWalletRequestBody {
    private String idempotencyKey;
    private String description;
    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getDescription() {
        return description;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
