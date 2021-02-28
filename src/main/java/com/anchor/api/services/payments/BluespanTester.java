package com.anchor.api.services.payments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluespanTester {
    public static final Logger LOGGER = LoggerFactory.getLogger(BluespanTester.class.getSimpleName());

    public static void main(String[] args) {

        getToken();
    }

    public static void getToken() {
        BlueSnapService service = new BlueSnapService();
        try {
            String res = service.getBlueSpanToken(75988.55);
            LOGGER.info("\uD83C\uDF3A \uD83C\uDF3A \uD83C\uDF3A " +
                    "Result of token request is :  \uD83C\uDF3A " + res + " \uD83C\uDF3A \uD83C\uDF3A \uD83C\uDF3A");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
