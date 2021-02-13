package com.anchor.api.services.stellar.circle.responses;

//CreateBankAccountResponse

import com.anchor.api.services.stellar.circle.models.BankAccount;
import com.anchor.api.services.stellar.circle.models.BankAddress;
import com.anchor.api.services.stellar.circle.models.BillingDetails;

public class CreateBankAccountResponse {
    Data data;
    public BankAccount getData() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(data.getId());
        bankAccount.setTrackingRef(data.getTrackingRef());
        bankAccount.setBankAddress(data.getBankAddress());
        bankAccount.setBillingDetails(data.getBillingDetails());
        bankAccount.setDescription(data.getDescription());
        bankAccount.setCreateDate(data.getCreateDate());
        bankAccount.setUpdateDate(data.getUpdateDate());
        bankAccount.setId(data.getId());

        return bankAccount;
    }
    public void setData(Data dataObject) {
        this.data = dataObject;
    }
}
class Data {
    private String id;
    private String description;
    private String trackingRef;
    BillingDetails BillingDetailsObject;
    BankAddress BankAddressObject;
    private String createDate;
    private String updateDate;


    // Getter Methods

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTrackingRef() {
        return trackingRef;
    }

    public BillingDetails getBillingDetails() {
        return BillingDetailsObject;
    }

    public BankAddress getBankAddress() {
        return BankAddressObject;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public void setBillingDetails(BillingDetails billingDetailsObject) {
        this.BillingDetailsObject = billingDetailsObject;
    }

    public void setBankAddress(BankAddress bankAddressObject) {
        this.BankAddressObject = bankAddressObject;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}




