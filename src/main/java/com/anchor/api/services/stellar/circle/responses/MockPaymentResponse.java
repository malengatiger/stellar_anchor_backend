package com.anchor.api.services.stellar.circle.responses;

public class MockPaymentResponse {

    private PaymentResponse data;
    public PaymentResponse getResponse() {
        return data;
    }

    public void setResponse(PaymentResponse response) {
        this.data = response;
    }
}

class PaymentResponseData {
    private  PaymentResponse paymentResponse;

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }
}


