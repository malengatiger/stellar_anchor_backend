package com.anchor.api.controllers.payments;


import com.anchor.api.services.payments.RipplePaymentService;
import com.anchor.api.util.E;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.xpring.ilp.model.AccountBalance;
import io.xpring.xrpl.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;


@RestController
@CrossOrigin(maxAge = 3600)
public class RippleController {
    public static final Logger LOGGER = LoggerFactory.getLogger(RippleController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private RipplePaymentService RipplePaymentService;

    public RippleController() {
        LOGGER.info(E.DICE.concat(E.DICE.concat("RIPPLE: XpringController ready to go ... \uD83C\uDF4E")));
    }

    @GetMapping(value = "/getXRPWallet", produces = MediaType.APPLICATION_JSON_VALUE)
    public Wallet getXRPWallet(String seed) throws Exception {
        LOGGER.info(E.DICE + "  Network Payment Services creating Wallet from seed ...  \uD83C\uDF4E ");
        Wallet wallet = RipplePaymentService.getXRPWallet(seed);
        String msg = E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... Network Payment Services: \uD83D\uDD35 \uD83D\uDD35 " +
                "Wallet created  ...  \uD83C\uDF4E \uD83C\uDFB2 ".concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return wallet;
    }
    @GetMapping(value = "/getXRPWalletBalance", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getXRPWalletBalance(String address) throws Exception {
        LOGGER.info(E.DICE + "  Network Payment Services: getXRPWalletBalance ...  \uD83C\uDF4E ");
        BigInteger xrpWalletBalance = RipplePaymentService.getXRPWalletBalance(address);
        String msg = E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... Network Payment Services: \uD83D\uDD35 \uD83D\uDD35 " +
                "XRP Wallet balance found  ...  \uD83C\uDF4E \uD83C\uDFB2 "
                        .concat(xrpWalletBalance.toString().concat(" "))
                        .concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return xrpWalletBalance.toString();
    }

    @GetMapping(value = "/getPayIdWalletBalance", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPayIdWalletBalance(String payId, String accountId, String accessToken) throws Exception {
        LOGGER.info(E.DICE + "....  Network Payment Services: getILPWalletBalance ...  \uD83C\uDF4E ");
        AccountBalance accountBalance = RipplePaymentService.getPayIdWalletBalance(payId,  accessToken);
        String msg = E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... Network Payment Services: \uD83D\uDD35 \uD83D\uDD35 " +
                "ILP (PayId) Wallet balance found  ...  \uD83C\uDF4E \uD83C\uDFB2 "
                        .concat(accountBalance.toString().concat(" "))
                        .concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return G.toJson(accountBalance);
    }

}
