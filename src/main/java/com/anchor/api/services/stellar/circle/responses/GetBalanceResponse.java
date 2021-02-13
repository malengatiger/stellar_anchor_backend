package com.anchor.api.services.stellar.circle.responses;


import com.anchor.api.services.stellar.circle.models.BalanceData;

public class GetBalanceResponse {
    BalanceData data;
    public BalanceData getBalance() {
        return data;
    }

    public void setData(BalanceData dataObject) {
        this.data = dataObject;
    }
}

