package com.anchor.api.services.payments;

public class Payer {
    private String partyIdType;
    private String partyId;

    public Payer(String partyIdType, String partyId) {
        this.partyIdType = partyIdType;
        this.partyId = partyId;
        if (this.partyIdType == null) {
            this.partyIdType = "MSISDN";
        }
    }

    public Payer() {
    }
    // Getter Methods

    public String getPartyIdType() {
        return partyIdType;
    }

    public String getPartyId() {
        return partyId;
    }

    // Setter Methods

    public void setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
}
