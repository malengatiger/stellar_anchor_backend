package com.anchor.api.data.models;

import com.bfn.client.data.TradeMatrixItemDTO;

import java.util.List;

public class NetworkOperatorDTO {

    private String issuedBy,
            accountId,
            date,
            name,
            email,
            cellphone,
            password,
            uid;
    private double minimumInvoiceAmount,
            maximumInvoiceAmount,
            maximumInvestment,
            tradeFrequencyInMinutes,
            defaultOfferDiscount;

    public List<TradeMatrixItemDTO> getTradeMatrixItems() {
        return tradeMatrixItems;
    }

    public void setTradeMatrixItems(List<TradeMatrixItemDTO> tradeMatrixItems) {
        this.tradeMatrixItems = tradeMatrixItems;
    }

    private List<TradeMatrixItemDTO> tradeMatrixItems;

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getMinimumInvoiceAmount() {
        return minimumInvoiceAmount;
    }

    public void setMinimumInvoiceAmount(double minimumInvoiceAmount) {
        this.minimumInvoiceAmount = minimumInvoiceAmount;
    }

    public double getMaximumInvoiceAmount() {
        return maximumInvoiceAmount;
    }

    public void setMaximumInvoiceAmount(double maximumInvoiceAmount) {
        this.maximumInvoiceAmount = maximumInvoiceAmount;
    }

    public double getMaximumInvestment() {
        return maximumInvestment;
    }

    public void setMaximumInvestment(double maximumInvestment) {
        this.maximumInvestment = maximumInvestment;
    }

    public double getTradeFrequencyInMinutes() {
        return tradeFrequencyInMinutes;
    }

    public void setTradeFrequencyInMinutes(double tradeFrequencyInMinutes) {
        this.tradeFrequencyInMinutes = tradeFrequencyInMinutes;
    }

    public double getDefaultOfferDiscount() {
        return defaultOfferDiscount;
    }

    public void setDefaultOfferDiscount(double defaultOfferDiscount) {
        this.defaultOfferDiscount = defaultOfferDiscount;
    }
}
