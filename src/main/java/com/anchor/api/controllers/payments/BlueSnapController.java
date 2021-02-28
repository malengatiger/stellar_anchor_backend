package com.anchor.api.controllers.payments;


import com.anchor.api.services.payments.BlueSnapService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(maxAge = 3600)
public class BlueSnapController {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlueSnapController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private BlueSnapService blueSnapService;

    public BlueSnapController() {
        LOGGER.info(E.DICE.concat(E.DICE.concat(
                "BlueSnapController ready to go ... \uD83C\uDF4E")));
    }

    @GetMapping(value = "/getBlueSnapToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public BlueSnapToken getBlueSnapToken(double amount) throws Exception {
        LOGGER.info(E.DICE + "BlueSnapController getBlueSnapToken  ...  \uD83C\uDF4E ");
        String blueSpanToken = blueSnapService.getBlueSpanToken(amount);
        String msg = E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... BlueSnapController: \uD83D\uDD35 \uD83D\uDD35 " +
                "Token created: " + blueSpanToken + " ...  \uD83C\uDF4E \uD83C\uDFB2 ".concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return new BlueSnapToken(blueSpanToken);
    }


}

