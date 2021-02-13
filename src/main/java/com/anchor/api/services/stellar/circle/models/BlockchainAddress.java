package com.anchor.api.services.stellar.circle.models;


public class BlockchainAddress {
    private String address;
    private String addressTag;
    private String currency;
    private String chain;

    public String getAddress() {
        return address;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public String getCurrency() {
        return currency;
    }

    public String getChain() {
        return chain;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }
}
