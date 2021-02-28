package com.anchor.api.util;

import com.anchor.api.services.stitch.StitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class myUUID {
    public static final Logger LOGGER = LoggerFactory.getLogger(myUUID.class.getSimpleName());
    public static void main(String[] args) {


        StitchService service = new StitchService();
        try {
            String jwt = service.getJWTToken();
            String res = service.getClientToken(jwt);

            LOGGER.info("\n\n✅ ✅ ✅  Result client access token: ✅ \n" + res );

            String m = service.getTransactions(res);

            LOGGER.info("\n\n✅ ✅ ✅  Result query: ✅ \n" + m );

//            🍎 🍎 LOGGER.info(" \uD83C\uDF3C Result nonce: \uD83C\uDF3C " + nonce );


        } catch (Exception e) {
            e.printStackTrace();
        }
//        LOGGER.info(E.RED_APPLE + E.RED_APPLE + "Generated UUID: " + UUID.randomUUID().toString());

    }
}

//REF: 8b8402d7-c55d-46f6-b372-17a5431e344b