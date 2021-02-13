package com.anchor.api.services.stellar.circle.models;

import com.anchor.api.services.stellar.circle.models.Balance;

import java.util.ArrayList;
import java.util.List;

public class BalanceData {
    List<Balance> available = new ArrayList<>();
    List<Balance> unsettled = new ArrayList<>();

    public List<Balance> getAvailable() {
        return available;
    }

    public void setAvailable(List<Balance> available) {
        this.available = available;
    }

    public List<Balance> getUnsettled() {
        return unsettled;
    }

    public void setUnsettled(List<Balance> unsettled) {
        this.unsettled = unsettled;
    }
}
