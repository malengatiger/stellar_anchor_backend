package com.anchor.api.services;


import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.util.logging.Logger;

@Service
public class PaymentService {
    public static final Logger LOGGER = Logger.getLogger(PaymentService.class.getSimpleName());
    private Server server;
    private Network network;
    private boolean isDevelopment;
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();


    @Autowired
    private FirebaseService firebaseService;

    @Value("${status}")
    private String status;

    @Value("${anchorName}")
    private String anchorName;

    @Value("${stellarUrl}")
    private String stellarUrl;


    public PaymentService() {
        LOGGER.info("\uD83C\uDF0D \uD83C\uDF0D PaymentService Constructor fired ... \uD83C\uDF0D");
    }

    private Anchor anchor;

    private SubmitTransactionResponse submit(PaymentRequest request) throws Exception {
        setServerAndNetwork();
        setAnchor(request.getAnchorId());

        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(request.getSeed());
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        if (request.getDestinationAccount().equalsIgnoreCase(sourceAccount.getAccountId())) {
            throw new Exception(Emoji.NOT_OK + "Source and destination accounts cannot be the same");
        }
        Asset asset = Asset.createNonNativeAsset(
                request.getAssetCode(),
                anchor.getIssuingAccount().getAccountId());
        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(new PaymentOperation.Builder(
                        request.getDestinationAccount(), asset, request.getAmount())
                        .build())
                .addMemo(Memo.text("Payment Tx"))
                .setTimeout(180)
                .setBaseFee(100)
                .build();

        transaction.sign(sourceKeyPair);
        return server.submitTransaction(transaction);
    }
    public PaymentRequest sendPayment(PaymentRequest paymentRequest) throws Exception {

        SubmitTransactionResponse transactionResponse = submit(paymentRequest);
        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(paymentRequest.getSeed());
        if (transactionResponse.isSuccess()) {
            //save to database
            String msg = Emoji.OK.concat(Emoji.HAND2.concat(Emoji.HAND2))
                    + "Payment Succeeded; \uD83D\uDD35  amount: "
                    .concat(paymentRequest.getAmount()).concat(" assetCode: ")
                    .concat(paymentRequest.getAssetCode()
                            .concat(" ").concat(paymentRequest.getDate())
                            .concat(" sourceAccount: ")
                            .concat(sourceKeyPair.getAccountId()).concat(" ").concat(Emoji.HAPPY));
            LOGGER.info(msg);
            paymentRequest.setLedger(transactionResponse.getLedger());
            paymentRequest.setDate(new DateTime().toDateTimeISO().toString());
            paymentRequest.setAnchorId(anchor.getAnchorId());
            paymentRequest.setSeed(null);
            paymentRequest.setSourceAccount(sourceKeyPair.getAccountId());
            firebaseService.addPaymentRequest(paymentRequest);
        } else {
            String err = Emoji.NOT_OK.concat(Emoji.ERROR) + "Payment Failed; \uD83D\uDD35  amount: "
                    .concat(paymentRequest.getAmount()).concat(" assetCode: ")
                    .concat(" ").concat(paymentRequest.getDate())
                    .concat(paymentRequest.getAssetCode().concat(" sourceAccount: ")
                    .concat(sourceKeyPair.getAccountId()));
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK).concat(err));
            AccountService.processPaymentError(transactionResponse);
        }
        return paymentRequest;

    }

    @Autowired
    private CryptoService cryptoService;

    public AgentFundingRequest fundAgent(AgentFundingRequest request) throws Exception {
        setServerAndNetwork();
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + ".... funding request coming in, check asset code ... : ".concat(G.toJson(request)));
        if (anchor == null) {
            setAnchor(request.getAnchorId());
        }
        Agent agent = firebaseService.getAgent(request.getAgentId());
        String seed = cryptoService.getDecryptedSeed(anchor.getDistributionAccount().getAccountId());

        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
        AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        Asset asset = Asset.createNonNativeAsset(
                request.getAssetCode(),
                anchor.getIssuingAccount().getAccountId());
        Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(new PaymentOperation.Builder(
                        agent.getStellarAccountId(), asset, request.getAmount())
                        .build())
                .addMemo(Memo.text("Payment Tx"))
                .setTimeout(180)
                .setBaseFee(100)
                .build();

        transaction.sign(sourceKeyPair);
        SubmitTransactionResponse transactionResponse = server.submitTransaction(transaction);

        if (transactionResponse.isSuccess()) {
            //save to database
            String msg = Emoji.OK.concat(Emoji.HAND2.concat(Emoji.HAND2))
                    + "Payment Succeeded; \uD83D\uDD35  amount: "
                    .concat(request.getAmount()).concat(" assetCode: ")
                    .concat(request.getAssetCode()
                            .concat(" ").concat(request.getDate())
                            .concat(" sourceAccount: ")
                            .concat(sourceKeyPair.getAccountId()).concat(" ").concat(Emoji.HAPPY));
            LOGGER.info(msg);
            firebaseService.addAgentFundingRequest(request);
        } else {
            String err = Emoji.NOT_OK.concat(Emoji.ERROR) + "Payment Failed; \uD83D\uDD35  amount: "
                    .concat(request.getAmount()).concat(" assetCode: ")
                    .concat(" ").concat(request.getDate())
                    .concat(request.getAssetCode().concat(" sourceAccount: ")
                            .concat(sourceKeyPair.getAccountId()));
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK).concat(err));
            AccountService.processPaymentError(transactionResponse);
        }
        return request;
    }

    private void setServerAndNetwork() {
        if (server != null) {
            return;
        }
        if (status == null) {
            LOGGER.info("\uD83D\uDE08 \uD83D\uDC7F Set status to dev because status is NULL");
            status = "dev";
        }
        isDevelopment = status.equalsIgnoreCase("dev");
        server = new Server(stellarUrl);
        if (isDevelopment) {
            network = Network.TESTNET;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F DEVELOPMENT: ... Stellar TestNet Server and Network ... \uD83C\uDF4F \uD83C\uDF4F \n");

        } else {
            network = Network.PUBLIC;
            LOGGER.info("\uD83C\uDF4F \uD83C\uDF4F PRODUCTION: ... Stellar Public Server and Network... \uD83C\uDF4F \uD83C\uDF4F \n");

        }
    }
    @Autowired
    private TOMLService tomlService;
    private void setAnchor(String anchorId) throws Exception {
        if (anchor != null) {
            return;
        }
        Toml toml = tomlService.getAnchorToml(anchorId);
        if (toml == null) {
            throw new Exception("anchor.toml has not been found. upload the file from your computer");
        } else {
            String id = toml.getString("anchorId");
            anchor = firebaseService.getAnchor(id);
            if (anchor == null) {
                LOGGER.info(Emoji.FIRE.concat(Emoji.FIRE.concat(Emoji.FIRE)
                        .concat("We are fucked! There is no ANCHOR !!!")));
            }
        }


    }

}
