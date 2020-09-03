package com.anchor.api.data.models.payfast;

public class PayFastTransactionDetails {
    private String m_payment_id, item_name, item_description, custom_str1,
            custom_str2, custom_str3, custom_str4, custom_str5;
    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getM_payment_id() {
        return m_payment_id;
    }

    public void setM_payment_id(String m_payment_id) {
        this.m_payment_id = m_payment_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public String getCustom_str1() {
        return custom_str1;
    }

    public void setCustom_str1(String custom_str1) {
        this.custom_str1 = custom_str1;
    }

    public String getCustom_str2() {
        return custom_str2;
    }

    public void setCustom_str2(String custom_str2) {
        this.custom_str2 = custom_str2;
    }

    public String getCustom_str3() {
        return custom_str3;
    }

    public void setCustom_str3(String custom_str3) {
        this.custom_str3 = custom_str3;
    }

    public String getCustom_str4() {
        return custom_str4;
    }

    public void setCustom_str4(String custom_str4) {
        this.custom_str4 = custom_str4;
    }

    public String getCustom_str5() {
        return custom_str5;
    }

    public void setCustom_str5(String custom_str5) {
        this.custom_str5 = custom_str5;
    }
}
