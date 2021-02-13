package com.anchor.api.services.stellar.circle.models;


import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private String walletId;
    private String entityId;
    private String type;
    private String description;
    List<BalanceData> balances = new ArrayList<BalanceData>();

    public String getWalletId() {
        return walletId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
