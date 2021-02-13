package com.anchor.api.services.stellar.circle.requests;


import com.anchor.api.services.stellar.circle.models.ACHAccount;
import com.anchor.api.services.stellar.circle.models.Balance;

public class CreateMockACHAccount {
    ACHAccount account;
    Balance balance;

    public ACHAccount getAccount() {
        return account;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setAccount(ACHAccount accountObject) {
        this.account = accountObject;
    }

    public void setBalance(Balance balanceObject) {
        this.balance = balanceObject;
    }
}

