package com.anchor.api.services.stellar.circle.responses;

import com.anchor.api.services.stellar.circle.models.BlockchainAddress;

public class GenerateBlockchainAddressResponse {
    BlockchainAddress data;

    public BlockchainAddress getData() {
        return data;
    }

    public void setData(BlockchainAddress data) {
        this.data = data;
    }
}
