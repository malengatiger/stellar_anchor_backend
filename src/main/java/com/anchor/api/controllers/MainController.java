package com.anchor.api.controllers;


import com.anchor.api.util.E;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(maxAge = 3600)
public class MainController {
    public static final Logger LOGGER = LoggerFactory.getLogger(MainController.class.getSimpleName());
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
}
