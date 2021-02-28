package com.anchor.api.services.payments;


import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Objects;

@Service
public class BlueSnapService {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlueSnapService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private final OkHttpClient client = new OkHttpClient();

    @Value("${bluesnapUsername}")
    private String bluesnapUsername;

    @Value("${bluesnapPassword}")
    private String bluesnapPassword;

    @Value("${bluesnapTokenURL}")
    private String bluesnapTokenURL;

    public static final MediaType JSON_MEDIA_TYPE
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML_MEDIA_TYPE
            = MediaType.parse("application/xml; charset=utf-8");

    public String getBlueSpanToken(double amount) throws Exception {
        LOGGER.info(E.DICE.concat(E.DICE).concat("getBlueSpanToken:... Posting to: "
                .concat(bluesnapTokenURL)));
        String xml = "<param-encryption xmlns=\"http://ws.plimus.com\">\n" +
                "  <parameters>\n" +
                "    <parameter>\n" +
                "      <param-key>amount</param-key>\n" +
                "      <param-value>"+ amount + "</param-value>\n" +
                "    </parameter>\n" +
                "  </parameters>\n" +
                "</param-encryption>";

        String m = bluesnapUsername + ":" + bluesnapPassword;
        String enc = Base64.getEncoder().encodeToString(m.getBytes());
        LOGGER.info(E.DICE.concat(E.DICE).concat("bluesnap base64 enc:  ♦️ " + enc + "  ♦️ "));

        String token;
        LOGGER.info(E.DICE.concat(E.DICE).concat("json:  \uD83D\uDD31 " + xml));
        RequestBody body = RequestBody.create(xml, XML_MEDIA_TYPE);
        LOGGER.info(E.DICE.concat(E.DICE).concat("body.contentType: ♦️ " + body.contentType()
                + " ♦️ contentLength: " + body.contentLength()));
        LOGGER.info(E.DICE.concat(E.DICE).concat("RequestBody : \uD83D\uDD31 " +
                "isOneShot: ♦️ " + body.isOneShot()));

        Request request = new Request.Builder()
                .url(bluesnapTokenURL)
                .addHeader("Accept", "application/xml")
                .addHeader("Content-Type", "application/xml")
                .addHeader("Authorization", "Basic " + enc)
                .post(body)
                .build();

            LOGGER.info(E.DICE.concat(E.DICE).concat(" \uD83E\uDD66 ... calling client.newCall(request) " +
                    "\uD83E\uDD66\uD83E\uDD66 " + request.toString()));

            Response response = client.newCall(request).execute();
            String stringResponse = Objects.requireNonNull(response.body()).string();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                File file = new File("mxml.xml");
                BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                writer.write(stringResponse);
                writer.close();
                Document document = builder.parse(file);
                NodeList nodeList = document.getElementsByTagName("encrypted-token");
                token = nodeList.item(0).getFirstChild().getTextContent();
                LOGGER.info(E.LEAF.concat(E.LEAF).concat("\uD83C\uDF3C \uD83C\uDF3C \uD83C\uDF3C " +
                        "myFuckingToken:: ♦️ " + token + " ♦️"));
                return token;

        } catch (Exception e) {
            LOGGER.info("\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F\uD83D\uDC7F" +
                    "WE ARE ROYALLY FUCKED !!!  \uD83D\uDD31  \uD83D\uDD31  \uD83D\uDD31 ");
            e.printStackTrace();
            LOGGER.info("Call to " + bluesnapTokenURL + " FAILED", e);
            throw new Exception("postXML Call Failed: " + bluesnapTokenURL);
        }

    }

    //https://sandbox.bluesnap.com/buynow/checkout?enc=6JbUHblKHHb5n9%2F2VJl8Iw%3D%3D&merchantid=902411
    private static class Amt {
        private double amount;

        public Amt(double m) {
            amount = m;
            System.out.println("amount: " + amount);
        }
    }
}
