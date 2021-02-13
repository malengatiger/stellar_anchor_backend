package com.anchor.api.services.payments;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.squareup.okhttp.MediaType;
import org.springframework.stereotype.Service;
import org.xrpl.rpc.v1.Payment;

import java.io.IOException;

import static org.springframework.http.MediaType.*;

/*
 💙 💙 💙 💙 💙 💙 💙 💙 💙 💙 TEST CARDS
Card number	Card provider
4007400000000007	Visa
4007410000000006	Visa
4200000000000000	Visa
4757140000000001	Visa
5102420000000006	Mastercard
5173375000000006	Mastercard
5555555555554444	Mastercard
 💙 💙 💙 💙 💙 💙 💙 💙 💙 💙 💙

😎 😎 😎 😎 😎 😎 😎 😎 😎 😎 😎 😎 CIRCLE URL's

Environment	API host
Sandbox	https://api-sandbox.circle.com
Production	https://api.circle.com
😎 😎 😎 😎 😎 😎 😎 😎 😎 😎 😎 😎
{
  "data": {
    "id": "74019380-5334-4cc2-b4a9-c83caf82c147",
    "description": "JPMORGAN CHASE BANK, NA ****6789",
    "trackingRef": "CIR2XJ5K4S",
    "billingDetails": {
      "name": "John Baker Smith",
      "line1": "1 Main Street",
      "city": "Boston",
      "postalCode": "02201",
      "district": "MA",
      "country": "US"
    },
    "bankAddress": {
      "bankName": "JPMORGAN CHASE BANK, NA",
      "city": "NEW YORK",
      "district": "NY",
      "country": "US"
    },
    "createDate": "2021-02-09T10:29:54.658Z",
    "updateDate": "2021-02-09T10:29:54.658Z"
  }
}

 🎽 🎽 Response from createBankAccount  🎽 🎽
{
  "data": {
    "id": "58666f84-fcf4-433f-aa2f-8d49159aa7f7",
    "description": "Standard Bank ****788A",
    "trackingRef": "CIR2TZC78Z",
    "billingDetails": {
      "name": "David John Carruthers",
      "line1": "1 Main Street",
      "city": "Pretoria",
      "postalCode": "0216",
      "district": "Gauteng",
      "country": "ZA"
    },
    "bankAddress": {
      "bankName": "Standard Bank",
      "city": "Sandton",
      "country": "ZA"
    },
    "createDate": "2021-02-11T02:59:31.511Z",
    "updateDate": "2021-02-11T02:59:31.511Z"
  }
 🎽 🎽 ........................................
}
 */

@Service
public class CircleServices {
    private static final Logger logger = LoggerFactory.getLogger(CircleServices.class.getName());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Value("circleAPIKey")
    private String circleAPIKey;

    @Value("spring.profiles.active")
    private String profile;

    private static final OkHttpClient client = new OkHttpClient();

    private static final String
            URL_DEV = "https://api-sandbox.circle.com",
            URL_PROD = "https://api.circle.com";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void sendPayment(Payment payment) {
        String url;
        if (profile.equalsIgnoreCase("dev")) {
            url = URL_DEV;
        } else {
            url = URL_PROD;
        }
        RequestBody body = RequestBody.create(JSON, gson.toJson(payment));
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + circleAPIKey)
                .method("POST", body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) {

            }
        });



    }

    public void getPayment() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.help").newBuilder();
        urlBuilder.addQueryParameter("v", "1.0");
        urlBuilder.addQueryParameter("user", "vogella");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "your token")
                .build();
    }



}
