package com.anchor.api.services.stellar.circle.responses;

import com.anchor.api.services.stellar.circle.models.Wallet;

public class CreateWalletResponse {
    Wallet data;

    public Wallet getData() {
        return data;
    }

    public void setData(Wallet data) {
        this.data = data;
    }
}
