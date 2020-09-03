package com.anchor.api.controllers.payments;

import com.anchor.api.data.models.ozow.OzowHash;
import com.anchor.api.data.models.ozow.OzowPaymentResponse;
import com.anchor.api.data.models.payfast.PayFastRequest;
import com.anchor.api.services.payments.HashCheckGenerator;
import com.anchor.api.services.misc.NetService;
import com.anchor.api.util.E;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(maxAge = 3600)
public class PayfastController {
    public static final Logger LOGGER = LoggerFactory.getLogger(PayfastController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public PayfastController() {
        LOGGER.info(E.PIG.concat(E.PIG.concat("PayFast Payments Controller up! ".concat(E.RED_APPLE))));
    }

    @Autowired
    private NetService netService;

    @PostMapping(value = "/payfast_return", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payfastSuccess(OzowPaymentResponse paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFast Payments RETURN Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments Success Callback"));
    }

    @PostMapping(value = "/payfast_cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payfastCancel(OzowPaymentResponse paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFastPayments CANCEL Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments cancel Callback"));
    }

    @PostMapping(value = "/payfast_error", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payfastError(OzowPaymentResponse paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFast Payments ERROR Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments error Callback"));
    }

    @PostMapping(value = "/payfast_notify", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payfastNotification(OzowPaymentResponse paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFast Payments NOTIFY Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments notify Callback"));
    }

    @Value("${payfast.merchantId}")
    private String merchantId;

    @Value("${payfast.merchantKey}")
    private String merchantKey;

    @Value("${countryCode}")
    private String countryCode;

    @Value("${currencyCode}")
    private String currencyCode;

    @Value("${payfast.returnUrl}")
    private String returnUrl;

    @Value("${payfast.cancelUrl}")
    private String cancelUrl;

    @Value("${payfast.notifyUrl}")
    private String notifyUrl;

    @Autowired
    private HashCheckGenerator hashCheckGenerator;


    @PostMapping(value = "/getPayfastSignature", produces = MediaType.APPLICATION_JSON_VALUE)
    public OzowHash getPayfastSignatureFromObject(@RequestBody PayFastRequest request) throws Exception {
        assert request != null;
        assert request.getTransactionDetails() != null;
        assert request.getTransactionDetails().getItem_name() != null;
        assert request.getTransactionDetails().getM_payment_id() != null;
        assert request.getTransactionDetails().getAmount() > 0.0;

        request.setMerchant_id(merchantId);
        request.setMerchant_key(merchantKey);
        request.setReturn_url(returnUrl);
        request.setCancel_url(cancelUrl);
        request.setNotify_url(notifyUrl);

        LOGGER.info(E.PIG.concat(E.PIG) + "Get PayFast Payments Signature: ".concat(G.toJson(request)));
        String hashed = hashCheckGenerator.generatePayfastSignature(request);
        LOGGER.info(E.HAND2.concat(E.HAND2.concat("PayFast Signature: ".concat(hashed))));
        return new OzowHash(hashed);
    }

    public void printPayfastCallbacks() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW)))
                .append("List of PayFast CALLBACKS used to interact with EFT services "
                .concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_YELLOW).append("RETURN CALLBACK: ".concat(returnUrl).concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_YELLOW).append("CANCEL CALLBACK: ".concat(cancelUrl).concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_YELLOW).append("NOTIFY CALLBACK: ".concat(notifyUrl).concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW)));

        LOGGER.info(sb.toString());
    }
    public static final String apples = E.RED_APPLE.concat(E.RED_APPLE.concat(E.RED_APPLE));
}
