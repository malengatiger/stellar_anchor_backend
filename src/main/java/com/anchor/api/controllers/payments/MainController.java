package com.anchor.api.controllers.payments;


import com.anchor.api.data.AccountInfoDTO;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.util.E;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(maxAge = 3600)
public class MainController {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Value(value = "${guru}")
    private String guru;

    @Value(value = "${skill}")
    private String skills;

    public MainController() {
        LOGGER.info(E.DICE.concat(E.DICE.concat("MainController up and away")));
    }

    @GetMapping(value = "/pingPay", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ping() throws Exception {
        LOGGER.info(E.DICE + " Pinging Network Payment Services via MainController and returning string ...  \uD83C\uDF4E ");
        return E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... Network Payment Services \uD83D\uDD35 \uD83D\uDD35 " +
                "Pinged ...  \uD83C\uDF4E \uD83C\uDFB2 skills that may be required: "
                        .concat(skills).concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
    }
    @GetMapping(value = "/helloPay", produces = MediaType.TEXT_PLAIN_VALUE)
    public String home() {
        String msg =
         "\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D \uD83C\uDF4E " +
                "NetworkPaymentServices says Hello World! ... using MainController, Boss! \uD83C\uDF4E "
                        .concat(guru).concat(" ".concat(E.YELLOW_BIRD.concat(E.BLUE_BIRD)));
        LOGGER.info(msg);
        return msg;
    }
    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/createBFNAccount", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponseBag createBFNAccount(@RequestBody AccountInfoDTO accountInfo) throws Exception {
        LOGGER.info(E.CHICKEN+E.CHICKEN + "MainController: .......... create Stellar account for BFN Account " + G.toJson(accountInfo));
        AccountResponseBag bag = accountService.addBFNAccount(accountInfo);
        String msg =
                "\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D \uD83C\uDF4E " +
                        "MainController is done adding BFN account to Stellar Anchor, Boss! \uD83C\uDF4E "
                                .concat(guru).concat(" ".concat(E.YELLOW_BIRD.concat(E.BLUE_BIRD)));
        LOGGER.info(msg);
        return bag;
    }
}
