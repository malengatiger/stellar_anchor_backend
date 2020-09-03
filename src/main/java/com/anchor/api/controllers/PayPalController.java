package com.anchor.api.controllers;

import com.anchor.api.services.HashCheckGenerator;
import com.anchor.api.services.NetService;
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
public class PayPalController {
    public static final Logger LOGGER = LoggerFactory.getLogger(PayPalController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public PayPalController() {
        LOGGER.info(E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayPal Payments Controller up! ".concat(E.YELLOW_BIRD))));
    }

    @Autowired
    private NetService netService;

    @PostMapping(value = "/paypal.webhook", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payPalWebHook(Object paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFast Payments RETURN Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments Success Callback"));
    }

    @PostMapping(value = "/paypal", produces = MediaType.APPLICATION_JSON_VALUE)
    public String payfastCancel(Object paymentResponse) throws Exception {
        LOGGER.info(E.PIG + " PayFastPayments CANCEL Callback ...".concat(G.toJson(paymentResponse)));
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat("PayFast Payments cancel Callback"));
    }

    @Value("${paypal.webhook}")
    private String payPalWebHook;

    @Value("${paypal.returnUrl}")
    private String payPalReturnUrl;

    @Autowired
    private HashCheckGenerator hashCheckGenerator;

    @PostMapping(value = "/getPayPalSignature", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPayPalSignature(@RequestBody Object request) throws Exception {

        LOGGER.info(E.PIG.concat(E.PIG) + "Get PayPal Payments Signature: ".concat(G.toJson(request)));

        return null;
    }

    public void printPayPalCallbacks() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(E.FLOWER_RED.concat(E.FLOWER_RED.concat(E.FLOWER_RED)))
                .append("List of PayPal CALLBACKS used to interact with EFT services "
                .concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_RED).append("PAYPAL RETURN CALLBACK: ".concat(payPalReturnUrl).concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_RED).append("PAYPAL WEBHOOK: ".concat(payPalWebHook).concat(" ".concat(E.RED_APPLE).concat("\n")));
        sb.append(E.FLOWER_RED.concat(E.FLOWER_RED.concat(E.FLOWER_RED)));

        LOGGER.info(sb.toString());
    }
    public static final String apples = E.RED_APPLE.concat(E.RED_APPLE.concat(E.RED_APPLE));
}
