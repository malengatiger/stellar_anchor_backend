package com.anchor.api.services.stitch;

import com.anchor.api.controllers.payments.BlueSnapController;
import com.anchor.api.util.E;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/*
Obtaining an Authorization Code
The first step in the process entails navigating the browser (or app) to the https://secure.stitch.money/connect/authorize endpoint. If the query string parameters passed to the endpoint are correctly formed, the user is redirected to the login and consent UI.

The table below lists the required parameters. Note that all values should be URL encoded. To complete the rest of the tutorial, you'll need at least the openid, accounts, balances, and offline_access scopes.

AUTHORIZATION REQUEST QUERY PARAMETERS
Parameter	Description
client_id	This is a unique ID that will be issued to you by a Stitch engineer
scope
A space separated list of requested scopes. The openid scope is required.

If you want to use a refresh token, request the offline_access scope.

Stitch API specific scopes are:
accounts
transactions
balances
accountholders

To determine which scopes are required for specific queries, please consult the Stitch API reference

response_type	Should always have a value of "code". Instructs Stitch SSO to return an authorization code
redirect_uri	One of a whitelisted set of URLs. After login, the user is redirected back to this URL. The redirect_uri is whitelisted to prevent open redirect attacks
nonce	A nonce is required to mitigate replay attacks. The value of the nonce should be a cryptographically secure random string, and is later included in the id_token, found in the token endpoint response.
state	The state parameter is required to prevent CSRF (Cross Site Request Forgery). Like the nonce, this should be a cryptographically secure random value. The value of the state parameter should be stored in the application (e.g. in local storage, or as a cookie). When the authorization request returns, the state is included, and should be validated against the stored value to properly protect against CSRF
code_challenge	A base64URL encoding of the SHA256 hashed code_verifier created below
code_challenge_method	The hash algorithm used for the code_challenge. In this case the value will be "S256" (case-sensitive)

 https://secure.stitch.money/connect/authorize?client_id=test-18fbd892-3b73-43c3-a854-c6f78c681349&scope=openid%20offline_access%20transactions%20accounts&response_type=code&redirect_uri=https%3A%2F%2Flocalhost%3A9000%2Freturn&state=2669382d-d6df-4331-a58b-d5743961578b&nonce=5138fa17-ed63-41e5-98b6-047f07efd940&code_challenge=FVF6VCwYJ_XljGdLXVjxs6g-_QRh4_CDutLOf_oIzPw&code_challenge_method=S256

 */
@Service
public class StitchService {
    public static final Logger LOGGER = LoggerFactory.getLogger(StitchService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    static final String STITCH_BASE_URL = "https://secure.stitch.money/";
    static final String STITCH_AUTH_URL = STITCH_BASE_URL + "connect/authorize?";

    @Value("${stitchClientId}")
    String clientId;
    @Value("${stitchRedirectUri}")
    String redirectUri;
    public static final MediaType JSON_MEDIA_TYPE
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();


    public String getTransactions(String accessToken) throws Exception {
        /*
        function queryStitchApi(accessToken) {
          return fetch('https://api.stitch.money/graphql', {
              'credentials': 'include',
              'headers': {
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${accessToken}`
              },
              'body': `{\"query\":\"query ListTransactions {\\n  user {\\n    __typename\\n    ... on UserInteractionRequired {\\n      userInteractionUrl\\n    }\\n    ... on User {\\n      id\\n      bankAccounts {\\n        name\\n      }\\n    }\\n  }\\n}\\n\",\"variables\":null,\"operationName\":\"ListTransactions\"}`,
              'method': 'POST',
              'mode': 'cors'
          });
        }

        query GetAccounts {
          user {
            bankAccounts {
              accountNumber
              accountType
              bankId
              branchCode
              id
              name
            }
          }
        }


         */
        String url = STITCH_BASE_URL + "graphql";
        String query = "query GetAccounts {\n" +
                "          user {\n" +
                "            bankAccounts {\n" +
                "              accountNumber\n" +
                "              accountType\n" +
                "              bankId\n" +
                "              branchCode\n" +
                "              id\n" +
                "              name\n" +
                "            }\n" +
                "          }\n" +
                "        }";

        String mm = String.valueOf(UTF_8.encode(query));
        LOGGER.info(mm);
        RequestBody body = RequestBody.create(mm, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();


        Response response = client.newCall(request).execute();

        String respBody = Objects.requireNonNull(response.body()).string();
        LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + "response: " + respBody));

        if (response.isSuccessful()) {
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF+ E.LEAF+ E.LEAF+ E.LEAF)
                    .concat("response.isSuccessful !!!, responseCode: "+ response.code()
                            + ", Got ourselves an access key!!! " +
                            "\uD83D\uDECE\uD83D\uDECE\uD83D\uDECE ") + respBody);
            return respBody;
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat(
                    "Response Code: : \uD83D\uDD31 " + response.code() + " body: " + respBody));
            throw new Exception(E.ERROR + "Boss, failed to do query, code: "
                    + response.code() + " " + response.message() + E.NOT_OK);
        }

    }
    public String getClientToken(String jwtToken) throws Exception {
       /*
         🍎 🍎  🍎 🍎  🍎 🍎
        We recommend that tokens are cached and only refreshed once expired as token generation is a
        cryptographically intensive process and so can slow down queries if retrieved on every request.
        */
        LOGGER.info(E.BLUE_DOT + E.BLUE_DOT + "Getting client token using jwt token ...... ");

        String url = STITCH_BASE_URL + "connect/token";

        String mClientId;
        if (clientId == null) {
            mClientId =  "test-3e463858-6832-49f3-a598-b2e4a9e14113";
        } else {
           mClientId = clientId;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("client_id", mClientId)
                .add("scope", "client_paymentrequest")
                .add("grant_type", "client_credentials")
                .add("audience", url)
                .add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                .add("client_assertion", jwtToken)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        String respBody = Objects.requireNonNull(response.body()).string();
        LOGGER.info(E.DICE.concat(E.DICE + E.LEAF + "response: " + respBody));
        JSONObject obj = new JSONObject(respBody);
        String accessToken = obj.getString("access_token");

        if (response.isSuccessful()) {
            LOGGER.info(E.DICE.concat(E.DICE + E.LEAF+ E.LEAF+ E.LEAF+ E.LEAF)
                    .concat("response.isSuccessful !!!, responseCode: "+ response.code()
                            + ", Got ourselves an access key!!! " +
                    "\uD83D\uDECE\uD83D\uDECE\uD83D\uDECE ") + accessToken);
            return accessToken;
        } else {
            LOGGER.info(E.ERROR.concat(E.ERROR + E.NOT_OK).concat(
                    "Response Code: : \uD83D\uDD31 " + response.code() + " body: " + respBody));
            throw new Exception(E.ERROR + "Boss, failed to get client token, code: "
                    + response.code() + " " + respBody + E.NOT_OK);
        }

    }


    public String getJWTToken() throws Exception {

        LOGGER.info(E.BLUE_DOT + E.BLUE_DOT + "Getting jwt token  ...... ");
        File filePublic, filePrivate;
        try {
             filePublic = ResourceUtils.getFile("classpath:cert.public");
            if (filePublic.exists()) {
                LOGGER.info(" \uD83C\uDF4E \uD83C\uDF4E cert.public exists at: " + filePublic.getAbsolutePath() + " size: " + filePublic.length() + " bytes");
            } else {
                LOGGER.info("......... new filePublic at: " + filePublic.getAbsolutePath());
                return "Public File Not found";
            }
             filePrivate= ResourceUtils.getFile("classpath:cert.private");
            if (filePrivate.exists()) {
                LOGGER.info(" \uD83C\uDF4E \uD83C\uDF4E cert.private  exists at: " + filePrivate.getAbsolutePath() + " size: " + filePrivate.length() + " bytes");
            } else {
                LOGGER.info("......... new filePrivate at: " + filePrivate.getAbsolutePath());
                return "PrivatFile Not found";
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new Exception("Files Fucked!");
        }
        String mClientId;
        if (clientId == null) {
            mClientId =  "test-3e463858-6832-49f3-a598-b2e4a9e14113";
        } else {
            mClientId = clientId;
        }


        RSAPublicKey publicKey = (RSAPublicKey) PemUtils.readPublicKeyFromFile(filePublic.getAbsolutePath(), "RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile(filePrivate.getAbsolutePath(), "RSA");

        Algorithm algorithmRS = Algorithm.RSA256(publicKey, privateKey);

        Instant currentInstant = Instant.now();
        String audience = STITCH_BASE_URL + "connect/token";
        String issuer = mClientId;
        String subject = mClientId;
        String jti = UUID.randomUUID().toString(); // Needs to be a unique value each time
        Date issuedAt = Date.from(currentInstant);
        Date expiresAt = Date.from(currentInstant.plusSeconds(60)); // Should be a small value after now

        LOGGER.info(E.BLUE_DIAMOND + E.BLUE_DIAMOND + " ................ creating access JWT token ...... "  + E.BLUE_DIAMOND);
        String clientJwt = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withSubject(subject)
                .withJWTId(jti)
                .withIssuedAt(issuedAt)
                .withNotBefore(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithmRS);

        LOGGER.info(E.LEAF + E.LEAF + " \uD83C\uDF4E \uD83C\uDF4E JWT : \uD83C\uDF4E \uD83C\uDF4E " + clientJwt);
        return clientJwt;
        /*
        curl -s -X POST -H "Content-Type:application/json" -d '{"query":"{ ping }"}' http://localhost:8080/graphql
{
  "data": {
    "ping": "OK"
  },
  "errors": [],
  "extensions": null
}
         */

    }


    public  String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
