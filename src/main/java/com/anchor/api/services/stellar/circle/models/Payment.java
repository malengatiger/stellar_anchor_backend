package com.anchor.api.services.stellar.circle.models;

import java.util.ArrayList;

public class Payment {
    private String id;
    private String type;
    private String merchantId;
    private String merchantWalletId;
    Source SourceObject;
    private String description;
    Amount AmountObject;
    Fees FeesObject;
    private String status;
    ArrayList< Object > refunds = new ArrayList < Object > ();
    private String createDate;
    private String updateDate;


    // Getter Methods 

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantWalletId() {
        return merchantWalletId;
    }

    public Source getSource() {
        return SourceObject;
    }

    public String getDescription() {
        return description;
    }

    public Amount getAmount() {
        return AmountObject;
    }

    public Fees getFees() {
        return FeesObject;
    }

    public String getStatus() {
        return status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    // Setter Methods 

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setMerchantWalletId(String merchantWalletId) {
        this.merchantWalletId = merchantWalletId;
    }

    public void setSource(Source sourceObject) {
        this.SourceObject = sourceObject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Amount amountObject) {
        this.AmountObject = amountObject;
    }

    public void setFees(Fees feesObject) {
        this.FeesObject = feesObject;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}

