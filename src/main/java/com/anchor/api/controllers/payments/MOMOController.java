package com.anchor.api.controllers.payments;


import com.anchor.api.services.payments.BlueSnapService;
import com.anchor.api.services.payments.MOMOService;
import com.anchor.api.services.payments.RequestToPayBody;
import com.anchor.api.services.payments.RequestToPayStatus;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(maxAge = 3600)
public class MOMOController {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOMOController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private MOMOService momoService;

    public MOMOController() {
        LOGGER.info(E.DICE.concat(E.DICE.concat(
                "BlueSnapController ready to go ... \uD83C\uDF4E")));
    }

    @GetMapping(value = "/getMomoApiKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public MOMOService.APIBag getMomoApiKey(String referenceId) throws Exception {
        LOGGER.info(E.DICE + "MOMOController getMomoApiKey  ...  \uD83C\uDF4E ");
        MOMOService.APIBag momoApiKey =momoService.getMomoApiKey(referenceId);
        String msg = E.FLOWER_RED + E.FLOWER_RED + E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... MOMOController: \uD83D\uDD35 \uD83D\uDD35 " +
                "MOMO sandbox API key created: " + momoApiKey.getApiKey() + " ...  \uD83C\uDF4E \uD83C\uDFB2 ".concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return momoApiKey;
    }
    @PostMapping(value = "/momoRequestToPay", produces = MediaType.APPLICATION_JSON_VALUE)
    public String momoRequestToPay(@RequestBody RequestToPayBag bag) throws Exception {
        LOGGER.info(E.DICE + "MOMOController momoRequestToPay  ...  \uD83C\uDF4E bag: "
                + G.toJson(bag));
        String response = momoService.requestToPay(bag);
        String msg = E.FLOWER_RED + E.FLOWER_RED + E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... MOMOController: \uD83D\uDD35 \uD83D\uDD35 " +
                "MOMO momoRequestToPay: " + response + " ...  \uD83C\uDF4E \uD83C\uDFB2 ".concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return response;
    }
    @GetMapping(value = "/getRequestToPayStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestToPayStatus getRequestToPayStatus(String referenceId) throws Exception {
        LOGGER.info(E.DICE + "MOMOController getRequestToPayStatus  ...  \uD83C\uDF4E ");
        RequestToPayStatus response = momoService.getRequestToPayStatus(referenceId);
        String msg = E.FLOWER_RED + E.FLOWER_RED + E.FLOWER_RED.concat(E.FLOWER_RED.concat(" ... MOMOController: \uD83D\uDD35 \uD83D\uDD35 " +
                "MOMO getRequestToPayStatus response: " + G.toJson(response) + " ...  \uD83C\uDF4E \uD83C\uDFB2 ".concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD))));
        LOGGER.info(msg);
        return response;
    }


    public static class RequestToPayBag {
        RequestToPayBody requestToPayBody;
        String referenceId;

        public RequestToPayBody getRequestToPayBody() {
            return requestToPayBody;
        }

        public void setRequestToPayBody(RequestToPayBody requestToPayBody) {
            this.requestToPayBody = requestToPayBody;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }
    }
}

