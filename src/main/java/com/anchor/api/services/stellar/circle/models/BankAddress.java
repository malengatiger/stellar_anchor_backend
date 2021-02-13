package com.anchor.api.services.stellar.circle.models;

public class BankAddress {
    private String country;
    private String bankName;
    private String city;


    // Getter Methods

    public String getCountry() {
        return country;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCity() {
        return city;
    }

    // Setter Methods

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
