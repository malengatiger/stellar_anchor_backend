package com.anchor.api.services.stellar.circle.responses;

import com.anchor.api.services.stellar.circle.models.Config;

public class GetConfigResponse {
    Config data;

    public Config getData() {
        return data;
    }

    public void setData(Config data) {
        this.data = data;
    }
}
