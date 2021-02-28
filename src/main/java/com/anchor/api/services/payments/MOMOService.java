package com.anchor.api.services.payments;

import com.anchor.api.controllers.payments.MOMOController;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;


@Service
public class MOMOService {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOMOService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient();

    public MOMOService() {
        LOGGER.info(E.GOLD_BELL + E.GOLD_BELL + "MOMOService constructing itself ... " + E.GOLD_BELL + E.GOLD_BELL);
    }

    @Value("${momoBaseUrl}")
    String momoBaseUrl;
    @Value("${momoSubscriptionKey}")
    String momoSubscriptionKey;

    @Value("${requesttopayURL}")
    String requesttopayURL;

    @Value("${momoHost}")
    String momoHost;

    @Value("${targetEnvironment}")
    String targetEnvironment;

    @Value("${momoAPIKey}")
    String momoAPIKey;

    public static final MediaType JSON_MEDIA_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    public static final String CONTENT_TYPE = "application/json", HOST = "momodeveloper.mtn.com";

    private static class Bag {
        String providerCallbackHost;

        public Bag(String providerCallbackHost) {
            this.providerCallbackHost = providerCallbackHost;
        }

        public String getProviderCallbackHost() {
            return providerCallbackHost;
        }

        public void setProviderCallbackHost(String providerCallbackHost) {
            this.providerCallbackHost = providerCallbackHost;
        }
    }

    public APIBag getMomoApiKey(String referenceId) throws Exception {

        assert JSON_MEDIA_TYPE != null;
        String mUrl = momoBaseUrl + "apiuser";
        LOGGER.info(E.HEART_BLUE + E.HEART_BLUE + "start process url: "
                + mUrl + " " + E.HEART_BLUE + " X-Reference: " + referenceId + E.HEART_ORANGE + " subs key: " + momoSubscriptionKey);

        Bag bag = new Bag("http://localhost:8084/ping");
        String json = G.toJson(bag);
        LOGGER.info(E.HEART_BLUE + E.HEART_BLUE + "request body json: " + json);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(mUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Host", momoHost)
                .addHeader("X-Reference-Id", referenceId)
                .addHeader("Ocp-Apim-Subscription-Key", momoSubscriptionKey)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF).concat("FIRST PART: response.isSuccessful !!!, GETTING the api key!!! " +
                    "\uD83D\uDECE\uD83D\uDECE\uD83D\uDECE response,  : \uD83D\uDD31 ") + response.toString());
            return getTheKey(referenceId);
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat("Response Code: : \uD83D\uDD31 " + response.code()));
            throw new Exception(E.ERROR + "Boss, we got a problem, code: " + response.code() + " " + E.NOT_OK);
        }

    }

    private APIBag getTheKey(String referenceId) throws Exception {
        assert JSON_MEDIA_TYPE != null;
        String mUrl = momoBaseUrl + "apiuser/" + referenceId + "/apikey";
        LOGGER.info(E.DICE.concat(E.DICE).concat("getTheKey: ....... url for apiKey request :" +
                " \uD83D\uDD31 .........") + mUrl);
        Bag bag = new Bag(null);
        RequestBody body = RequestBody.create(G.toJson(bag), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(mUrl)
                .addHeader("Host", momoHost)
                .addHeader("Ocp-Apim-Subscription-Key", momoSubscriptionKey)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String stringResponse = Objects.requireNonNull(response.toString());
            String responseBody = Objects.requireNonNull(response.body()).string();
            APIBag apiBag = G.fromJson(responseBody, APIBag.class);
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("getTheKey: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE stringResponse,  : \uD83D\uDD31 ") + stringResponse);
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("getTheKey: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE responseBody,  : \uD83D\uDD31 ") + G.toJson(apiBag));
            return apiBag;
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat("getTheKey: Response:: \uD83D\uDD31 "
                    + response.toString()));
            throw new Exception(E.ERROR + "Boss, we got api key problem, code: " + response.code() + " " + E.NOT_OK);
        }

    }

    public static class APIBag {
        String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }


    public String requestToPay(MOMOController.RequestToPayBag requestToPayBag) throws Exception{
        assert JSON_MEDIA_TYPE != null;
        assert requestToPayBag != null;
        String mUrl = requesttopayURL;
        String mJSON = G.toJson(requestToPayBag);

        LOGGER.info(E.DICE.concat(E.DICE).concat("MOMO requestToPay: ....... url :" +
                " \uD83D\uDD31 .........") + mUrl + " Target Env: " + targetEnvironment);
        LOGGER.info(E.DICE.concat(E.DICE).concat("MOMO requestToPay: ....... json bag:" +
                " \uD83D\uDD31 .........") + mJSON);

        RequestBody body = RequestBody.create(G.toJson(requestToPayBag.getRequestToPayBody()), JSON_MEDIA_TYPE);
        String auth = requestToPayBag.getReferenceId() + ":" + momoAPIKey;
        LOGGER.info(E.PEACH + E.PEACH + "...  Basic auth before encoding : " + auth);
        byte[] bytes = Base64.getEncoder().encode((auth).getBytes());
        String basic = new String(bytes);
        //Basic authentication header containing API user ID and API key. Should be sent in as B64 encoded.
        LOGGER.info(E.PEACH + E.PEACH + "... encoded Basic Base64 auth : " + basic);
        Request request = new Request.Builder()
                .url(mUrl)
                .addHeader("Ocp-Apim-Subscription-Key", momoSubscriptionKey)
//                .addHeader("X-Callback-Url", "http://somewhere.com")
                .addHeader("X-Reference-Id", requestToPayBag.getReferenceId())
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Target-Environment", targetEnvironment)
                .addHeader("Authorization", "Basic " + basic)
                .post(body)
                .build();

        LOGGER.info(E.PEACH + E.PEACH + "requestToPay: \n" + request.toString());

        LOGGER.info(E.PEACH + E.PEACH + "headers:" + E.PEACH + E.PEACH + "\n" + request.headers().toString());

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String stringResponse = Objects.requireNonNull(response.toString());
            String responseBody = Objects.requireNonNull(response.body()).string();
            APIBag apiBag = G.fromJson(responseBody, APIBag.class);
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("requestToPay: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE stringResponse,  : \uD83D\uDD31 ") + stringResponse);
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("requestToPay: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE responseBody,  : \uD83D\uDD31 ") + G.toJson(apiBag));
            return responseBody;
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat("requestToPay: Response:: \uD83D\uDD31 "
                    + response.toString() + " " + Objects.requireNonNull(response.networkResponse()).message()));
            throw new Exception(E.ERROR + "Boss, we got a requestToPay problem, code: "
                    + response.code() + " " + E.NOT_OK);
        }

    }
    public RequestToPayStatus getRequestToPayStatus(String referenceId) throws Exception{
        assert JSON_MEDIA_TYPE != null;
        String mUrl = requesttopayURL + "/" + referenceId;

        LOGGER.info(E.DICE.concat(E.DICE).concat("getRequestToPayStatus: ....... url :" +
                " \uD83D\uDD31 .........") + mUrl);

        Request request = new Request.Builder()
                .url(mUrl)
                .addHeader("Ocp-Apim-Subscription-Key", momoSubscriptionKey)
                .addHeader("X-Target-Environment", targetEnvironment)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String stringResponse = Objects.requireNonNull(response.toString());
            String responseBody = Objects.requireNonNull(response.body()).string();
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("getRequestToPayStatus: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE stringResponse,  : \uD83D\uDD31 ") + stringResponse);
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + E.LEAF + E.LEAF).concat("getRequestToPayStatus: response.isSuccessful !!!, " + response.code() +
                    " \uD83D\uDECE\uD83D\uDECE\uD83D\uDECE responseBody,  : \uD83D\uDD31 ") + responseBody);
            return G.fromJson(responseBody, RequestToPayStatus.class);
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat("getRequestToPayStatus: Response:: \uD83D\uDD31 "
                    + response.toString()));
            throw new Exception(E.ERROR + "Boss, we got getRequestToPayStatus problem, code: " + response.code() + " " + E.NOT_OK);
        }

    }

}
