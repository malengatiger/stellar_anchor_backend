package com.anchor.api.services.stellar;


import com.anchor.api.data.AccountInfoDTO;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.transfer.sep10.AnchorSep10Challenge;
import com.anchor.api.services.misc.CryptoService;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.services.misc.TOMLService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.Asset;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.*;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.RootResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.xdr.*;
import shadow.com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AccountService {
    public static final Logger LOGGER = Logger.getLogger(AccountService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String FRIEND_BOT = "https://friendbot.stellar.org/?addr=%s";
    private static final int TIMEOUT_IN_SECONDS = 180;
    private boolean isDevelopment;
    private Server server;
    private Network network;

    public AccountService() {
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C AccountService Constructor fired ... \uD83C\uDF3C Manage Stellar Accounts");
    }

    public void printStellarHorizonServer() {
        setServerAndNetwork();
        try {
            final RootResponse serverResponse = server.root();
            LOGGER.info("\uD83E\uDD8B \uD83E\uDD8B \uD83C\uDF3C Stellar Horizon Server: "
                    .concat(G.toJson(serverResponse).concat(" \uD83C\uDF3C \uD83C\uDF3C ")));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private FirebaseService firebaseService;

    @Value("${status}")
    private String status;

    @Value("${defaultCurrencies}")
    private String defaultCurrencies;

    @Value("${stellarUrl}")
    private String stellarUrl;

    @Value("${domain}")
    private String domain;

    public void talkToFriendBot(final String accountId) throws IOException {
        LOGGER.info("\uD83E\uDD6C ... Begging Ms. FriendBot for some \uD83C\uDF51 pussy \uD83C\uDF51 ... "
                + " \uD83D\uDD34 I heard she gives out!!");
        isDevelopment = status.equalsIgnoreCase("dev");
        setServerAndNetwork();
        InputStream response;
        final String friendBotUrl = String.format(FRIEND_BOT, accountId);
        response = new URL(friendBotUrl).openStream();
        final String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        LOGGER.info("\uD83E\uDD6C "
                + "FriendBot responded with largess: \uD83E\uDD6C 10,000 Lumens obtained. ... Yebo, Gogo!! \uD83E\uDD6C ");

        if (isDevelopment) {
            LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Booty from Ms. FriendBot: \uD83C\uDF51 " + body);
        }
    }

    @Autowired
    private TOMLService tomlService;
    private Anchor anchor;

    private void setAnchor() throws Exception {
        if (anchor != null) {
            return;
        }
        anchor = firebaseService.getAnchor();
    }

    public AccountResponse getAccountUsingSeed(final String seed) throws IOException {
        setServerAndNetwork();
        final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
        final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());

        return sourceAccount;
    }

    // todo - ensure auth as anchor user for this call
    public AccountResponse getAccountUsingAccount(final String accountId) throws Exception {
        setServerAndNetwork();
        LOGGER.info(E.PEAR + " ..... Getting account: " + accountId);
        try {
            return server.accounts().account(accountId);
        } catch (Exception e) {
            LOGGER.info(E.NOT_OK.concat(E.NOT_OK) + "getAccountUsingAccount FAILED");
            e.printStackTrace();
            throw new Exception(E.ERROR + "Failed to get Account data from Stellar");
        }
    }

    public String retrieveKey(final String agentId) throws Exception {
        final Agent mAgent = firebaseService.getAgent(agentId);
        if (mAgent == null) {
            throw new Exception("Agent not found");
        }
        final String seed = cryptoService.getDecryptedSeed(mAgent.getStellarAccountId());
        LOGGER.info("Seed decrypted and returned");
        return seed;
    }

    private static final int STELLAR_TIMEOUT_IN_SECONDS = 360, STELLAR_BASE_FEE = 100;

    public AccountResponseBag createAndFundAnchorAccount(
            final String seed,
            final String startingBalance)
            throws Exception {
        LOGGER.info("\n\n\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 \uD83C\uDF4E" +
                " ... ... ... ... AccountService: createAndFundAnchorAccount starting ....... startingBalance: "
                + startingBalance);

        setServerAndNetwork();
        AccountResponse accountResponse;
        AccountResponse sourceAccount;

        try {
            final KeyPair newAccountKeyPair = KeyPair.random();
            final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(seed);
            final String secret = new String(newAccountKeyPair.getSecretSeed());
            try {
                sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
            } catch (Exception e) {
                LOGGER.info(E.ERROR+E.ERROR
                        + " Basic Stellar SDK call not working. What the fuck!!! "+E.ERROR+E.ERROR+E.ERROR);
                LOGGER.severe(E.NOT_OK + E.NOT_OK +
                        " Unable to obtain accountResponse for the funding seed account on Stellar "
                + E.ERROR);
                throw e;
            }

            final Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(new CreateAccountOperation.Builder(newAccountKeyPair.getAccountId(), startingBalance)
                            .build())
                    .addMemo(Memo.text("CreateAccount Tx"))
                    .setTimeout(STELLAR_TIMEOUT_IN_SECONDS)
                    .setBaseFee(STELLAR_BASE_FEE).build();

            transaction.sign(sourceKeyPair);

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);

            if (submitTransactionResponse.isSuccess()) {
                accountResponse = server.accounts().account(newAccountKeyPair.getAccountId());
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  "
                        + "Stellar account has been created and funded!: \uD83C\uDF4E \uD83C\uDF4E YEBO!!!");
                final AccountResponseBag bag = new AccountResponseBag(accountResponse, secret);
                LOGGER.info(("\uD83C\uDF4E \uD83C\uDF4E RESPONSE from Stellar; " + "\uD83D\uDC99 new accountId: ")
                        .concat(bag.getAccountResponse().getAccountId()));
                cryptoService.encrypt(bag.getAccountResponse().getAccountId(), secret);
                return bag;
            } else {
                LOGGER.info("\uD83D\uDC7F \uD83D\uDE21 Things have not gone well, Senor! " +
                        "\uD83D\uDC7F \uD83D\uDE21 submitTransactionResponse: " + submitTransactionResponse.toString());
                if (submitTransactionResponse.getExtras() != null) {
                    SubmitTransactionResponse.Extras.ResultCodes codes = submitTransactionResponse.getExtras().getResultCodes();
                    LOGGER.info("\uD83D\uDC7F \uD83D\uDE21 SubmitTransactionResponse: getTransactionResultCode: " + codes.getTransactionResultCode());
                    if (codes.getTransactionResultCode().contains("tx_failed")) {
                        LOGGER.info(E.PEPPER + E.PEPPER + "CreateAccount Transaction failed ".concat(E.NOT_OK + E.NOT_OK));
                        for (String code : codes.getOperationsResultCodes()) {
                            LOGGER.info(E.PEPPER + E.PEPPER + "OperationsResultCode: " + code + " " + E.NOT_OK);
                        }
                    }
                }
                throw new Exception(E.PEPPER + E.PEPPER + "Stellar CreateAccount Transaction failed ".concat(E.NOT_OK));

            }

        } catch (final Exception e) {
            LOGGER.info(E.ERROR + E.ERROR + "Failed to create account - see below ...");
            e.printStackTrace();
            throw new Exception("\uD83D\uDD34 Unable to create Account \uD83D\uDD34");
        }
    }

    private void processAccountCreationError(CreateAccountResult createAccountResult) throws Exception {

        switch (createAccountResult.getDiscriminant().getValue()) {
            case -1:
                LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction is MALFORMED");
                throw new Exception("CREATE_ACCOUNT_MALFORMED");
            case -2:
                LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction is UNDER FUNDED");
                throw new Exception("CREATE_ACCOUNT_UNDERFUNDED");
            case -3:
                LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction has ACCOUNT_LOW_RESERVE");
                throw new Exception("CREATE_ACCOUNT_LOW_RESERVE");
            case -4:
                LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 transaction ACCOUNT_ALREADY_EXISTS");
                throw new Exception("CREATE_ACCOUNT_ALREADY_EXISTS");
            default:
                throw new Exception("CreateAccountOperation transactionResponse is NOT success");
        }
    }

    public AccountResponseBag createAndFundUserAccount(final String startingXLMBalance,
                                                       final String startingFiatBalance, final String fiatLimit) throws Exception {
        LOGGER.info(E.PEAR.concat(E.PEAR) + "\uD83D\uDC99 ... ... ... ... createAndFundUserAccount starting "
                + "....... startingXLMBalance: " + startingXLMBalance + " startingFiatBalance:" + startingFiatBalance
                + " fiatLimit: " + fiatLimit);
        setServerAndNetwork();
        setAnchor();
        if (anchor == null) {
            throw new Exception("Anchor is missing! Big problem, Boss! Please check collection names used");
        }
        AccountResponseBag accountResponseBag;
        try {
            LOGGER.info(E.PEAR.concat(E.PEAR.concat("Getting encrypted seed (BaseAccount AccountId)from storage; \uD83C\uDF4E \uD83C\uDF4E will decrypt and use ...")));
            final String baseSeed = cryptoService.getDecryptedSeed(anchor.getBaseAccount().getAccountId());
            final KeyPair keyPair = KeyPair.random();
            final KeyPair sourceKeyPair = KeyPair.fromSecretSeed(baseSeed);

            final AccountResponse baseAccount = server.accounts().account(sourceKeyPair.getAccountId());

            LOGGER.info(E.PEAR.concat(E.PEAR.concat("Getting encrypted seed (DistributionAccount AccountId)from storage; " +
                    "\uD83C\uDF4E \uD83C\uDF4E will decrypt and use ...")));

            final String distributionSeed = cryptoService
                    .getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
            final KeyPair distributionKeyPair = KeyPair.fromSecretSeed(distributionSeed);

            final Transaction transaction = new Transaction.Builder(baseAccount, network)
                    .addOperation(
                            new CreateAccountOperation.Builder(keyPair.getAccountId(), startingXLMBalance).build())
                    .addMemo(Memo.text("CreateAccount Tx")).setTimeout(180).setBaseFee(100).build();

            transaction.sign(sourceKeyPair);
            LOGGER.info(E.RED_CAR + E.RED_CAR + E.RED_CAR + " ... Submit tx with CreateAccountOperation to Stellar ... ");
            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);

            if (submitTransactionResponse.isSuccess()) {
                // add trustLines and first payment for all fiat tokens
                LOGGER.info(E.LEAF.concat(E.LEAF).concat(E.LEAF).concat(E.LEAF)
                        + "Stellar account created: ".concat(E.LEAF).concat(E.LEAF).concat(" ")
                        .concat(keyPair.getAccountId()).concat(" ... about to start creating trustLines ..."));
                accountResponseBag = addTrustLinesAndOriginalBalances(fiatLimit,
                        startingFiatBalance, keyPair, distributionKeyPair);
                LOGGER.info(E.LEAF.concat(E.LEAF.concat(E.LEAF))
                        + " .......... It is indeed possible that everything worked. \uD83D\uDE21 WTF?");
                final String secret = new String(keyPair.getSecretSeed());
                accountResponseBag.setSecretSeed(secret);

                cryptoService.encrypt(accountResponseBag.getAccountResponse().getAccountId(), secret);
                return accountResponseBag;
            } else {
                if (submitTransactionResponse.getExtras() != null) {
                    SubmitTransactionResponse.Extras.ResultCodes codes = submitTransactionResponse.getExtras().getResultCodes();
                    if (codes.getTransactionResultCode().contains("tx_failed")) {
                        LOGGER.info(E.PEPPER + E.PEPPER + "CreateAccount Transaction failed ".concat(E.NOT_OK + E.NOT_OK));
                        for (String code : codes.getOperationsResultCodes()) {
                            LOGGER.info(E.PEPPER + E.PEPPER + "OperationsResultCode: " + code + " " + E.NOT_OK);
                        }
                    }
                }
                throw new Exception(E.PEPPER + E.PEPPER + "Stellar CreateAccount Transaction failed ".concat(E.NOT_OK));
            }

        } catch (final IOException e) {
            LOGGER.severe("Failed to create account - see below ...");
            throw new Exception("\uD83D\uDD34 Unable to create Account", e);
        }
    }

    private AccountResponseBag addTrustLinesAndOriginalBalances(final String limit, final String startingFiatBalance,
                                                                final KeyPair userKeyPair, final KeyPair distributionKeyPair) throws Exception {

        final List<AssetBag> assetBags = getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(E.PEAR.concat(E.PEAR)
                .concat(("addTrustLinesAndOriginalBalances: limit: "
                        + limit + " startingFiatBalance: " + startingFiatBalance
                        + "  \uD83C\uDF45 Building transaction with trustLine operations ... FIAT ASSETS: " + assetBags.size())
                        .concat(" " + E.RED_DOT + E.RED_DOT)));
        final AccountResponse account = server.accounts().account(userKeyPair.getAccountId());
        final Transaction.Builder trustLineTxBuilder = new Transaction.Builder(account, network);
        for (final AssetBag assetBag : assetBags) {
            trustLineTxBuilder.addOperation(new ChangeTrustOperation.Builder(assetBag.asset, limit).build());
        }
        final Transaction userTrustLineTx = trustLineTxBuilder.addMemo(Memo.text("User TrustLine Tx")).setTimeout(180)
                .setBaseFee(100).build();

        userTrustLineTx.sign(userKeyPair);
        LOGGER.info(E.PEAR.concat(E.PEAR)
                .concat(("addTrustLinesAndOriginalBalances: " + "Submitting transaction with trustLine operations ... ")
                        .concat(" " + E.RED_DOT + E.RED_DOT)));
        final SubmitTransactionResponse trustLineTransactionResponse = server.submitTransaction(userTrustLineTx);
        LOGGER.info(
                E.HAND1.concat(E.HAND2.concat(E.HAND3)) + "User TrustLine transaction response; isSuccess: "
                        .concat("" + trustLineTransactionResponse.isSuccess()));
        if (trustLineTransactionResponse.isSuccess()) {
            return sendFiatPayments(startingFiatBalance, userKeyPair, distributionKeyPair, assetBags);
        } else {
//
//            TransactionResult transactionResult = trustLineTransactionResponse.getDecodedTransactionResult().get();
//            AllowTrustResult allowTrustResult = null;
//            for (OperationResult result : transactionResult.getResult().getResults()) {
//                if (result.getTr().getCreateAccountResult() != null) {
//                    allowTrustResult = result.getTr().getAllowTrustResult();
//                }
//            }
//            if (allowTrustResult == null) {
//                throw  new Exception("AllowTrustOperation failed");
//            }
//            switch (allowTrustResult.getDiscriminant().getValue()) {
//                case -1:
//                    LOGGER.info(Emoji.NOT_OK+Emoji.NOT_OK+Emoji.NOT_OK + "ALLOW_TRUST_MALFORMED");
//                  throw new Exception("ALLOW_TRUST_MALFORMED");
//                case -2:
//                    LOGGER.info(Emoji.NOT_OK+Emoji.NOT_OK+Emoji.NOT_OK + "ALLOW_TRUST_NO_TRUST_LINE");
//                    throw new Exception("ALLOW_TRUST_NO_TRUST_LINE");
//                case -3:
//                    LOGGER.info(Emoji.NOT_OK+Emoji.NOT_OK+Emoji.NOT_OK + "ALLOW_TRUST_TRUST_NOT_REQUIRED");
//                    throw new Exception("ALLOW_TRUST_TRUST_NOT_REQUIRED");
//                case -4:
//                    LOGGER.info(Emoji.NOT_OK+Emoji.NOT_OK+Emoji.NOT_OK + "ALLOW_TRUST_CANT_REVOKE");
//                    throw new Exception("ALLOW_TRUST_CANT_REVOKE");
//                case -5:
//                    LOGGER.info(Emoji.NOT_OK+Emoji.NOT_OK+Emoji.NOT_OK + "ALLOW_TRUST_SELF_NOT_ALLOWED");
//                    throw new Exception("ALLOW_TRUST_SELF_NOT_ALLOWED");
//                default:
//                    String msg = Emoji.NOT_OK.concat(Emoji.NOT_OK.concat(Emoji.ERROR)
//                            .concat("Trustline Transaction failed "));
//                    LOGGER.info(msg);
//                    throw new Exception(msg);
//            }
            if (trustLineTransactionResponse.getExtras() != null) {
                SubmitTransactionResponse.Extras.ResultCodes codes = trustLineTransactionResponse.getExtras().getResultCodes();
                if (codes.getTransactionResultCode().contains("tx_failed")) {
                    LOGGER.info(E.PEPPER + E.PEPPER + "CreateAccount Transaction failed ".concat(E.NOT_OK + E.NOT_OK));
                    for (String code : codes.getOperationsResultCodes()) {
                        LOGGER.info(E.PEPPER + E.PEPPER + "OperationsResultCode: " + code + " " + E.NOT_OK);
                    }
                }
            }
            throw new Exception(E.PEPPER + E.PEPPER + "Stellar CreateAccount Transaction failed ".concat(E.NOT_OK));
        }

    }

    private AccountResponseBag sendFiatPayments(final String amount, final KeyPair destinationKeyPair,
                                                final KeyPair sourceKeyPair, final List<AssetBag> assetBags) throws Exception {

        LOGGER.info(E.BLUE_BIRD.concat(E.BLUE_BIRD)
                .concat("sendFiatPayments: Creating payment transaction ... " + assetBags.size()
                        + " FIAT assets to be paid; destinationAccount: ".concat(destinationKeyPair.getAccountId())
                        .concat(" sourceAccount: ").concat(sourceKeyPair.getAccountId()).concat(E.FIRE)
                        .concat(E.FIRE))
                .concat(" ----- check AMOUNT: ").concat(amount));

        setServerAndNetwork();
        final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        final Transaction.Builder paymentTxBuilder = new Transaction.Builder(sourceAccount, network);
        for (final AssetBag assetBag : assetBags) {
            paymentTxBuilder.addOperation(
                    new PaymentOperation.Builder(destinationKeyPair.getAccountId(), assetBag.asset, amount).build());
        }
        final Transaction paymentTx = paymentTxBuilder.addMemo(Memo.text("User Payment Tx")).setTimeout(180)
                .setBaseFee(100).build();

        paymentTx.sign(sourceKeyPair);

        LOGGER.info(E.PEAR.concat(E.PEAR).concat(
                ("sendPayment: " + "Submitting transaction with payment operations ... ").concat(E.RED_DOT)));
        final SubmitTransactionResponse payTransactionResponse = server.submitTransaction(paymentTx);
        if (payTransactionResponse.isSuccess()) {
            final String msg = E.LEAF.concat(E.LEAF.concat(E.LEAF)
                    .concat("Payment Transaction is successful. \uD83D\uDE21 Check fiat balances on user account \uD83D\uDE21"));
            LOGGER.info(msg);
            final AccountResponse userAccountResponse = server.accounts().account(destinationKeyPair.getAccountId());
            final String seed = new String(destinationKeyPair.getSecretSeed());
            final AccountResponseBag bag = new AccountResponseBag(userAccountResponse, seed);
            // add original payments to database
            for (final AssetBag assetBag : assetBags) {
                final PaymentRequest request = new PaymentRequest();
                request.setAmount(amount);
                request.setAnchorId(anchor.getAnchorId());
                request.setDate(new DateTime().toDateTimeISO().toString());
                request.setAssetCode(assetBag.assetCode);
                request.setSourceAccount(sourceKeyPair.getAccountId());
                request.setDestinationAccount(destinationKeyPair.getAccountId());
                request.setLedger(payTransactionResponse.getLedger());
                request.setPaymentRequestId(UUID.randomUUID().toString());
                firebaseService.addPaymentRequest(request);
            }
            return bag;
        } else {
            processPaymentError(payTransactionResponse);
        }
        return null;
    }

    private PaymentRequest sendAssetPayment(final String sourceSeed,
                                            final String destinationAccountId,
                                            final String amount,
                                            final String assetCode,
                                            final String memo) throws Exception {

        if (sourceSeed == null) {
            throw new IllegalArgumentException("Source seed not found");
        }
        if (destinationAccountId == null) {
            throw new IllegalArgumentException("destinationAccountId not found");
        }
        if (assetCode == null) {
            throw new IllegalArgumentException("Asset Code not found");
        }
        KeyPair sourceKeyPair = KeyPair.fromSecretSeed(sourceSeed);
        LOGGER.info(E.BLUE_BIRD.concat(E.BLUE_BIRD)
                .concat("sendAssetPayment: Creating payment transaction ... " + assetCode
                        + " FIAT asset to be paid; destinationAccountId: ".concat(destinationAccountId)
                        .concat(" sourceAccount: ").concat(sourceKeyPair.getAccountId()).concat(E.FIRE)
                        .concat(E.FIRE))
                .concat(" ----- check AMOUNT: ").concat(amount));
        setAnchor();
        List<AccountService.AssetBag> assetBags = getDefaultAssets(anchor.getIssuingAccount().getAccountId());

        Asset asset = null;
        for (AssetBag assetBag : assetBags) {
            if (assetBag.assetCode.equalsIgnoreCase(assetCode)) {
                asset = assetBag.asset;
                break;
            }
        }
        if (asset == null) {
            throw new Exception("Asset ".concat(assetCode).concat(" not found"));
        }
        setServerAndNetwork();
        final AccountResponse sourceAccount = server.accounts().account(sourceKeyPair.getAccountId());
        final Transaction.Builder paymentTxBuilder = new Transaction.Builder(sourceAccount, network);
        paymentTxBuilder.addOperation(
                new PaymentOperation.Builder(destinationAccountId, asset, amount).build());
        final Transaction paymentTx = paymentTxBuilder.addMemo(Memo.text(memo)).setTimeout(180)
                .setBaseFee(100).build();

        paymentTx.sign(sourceKeyPair);
        LOGGER.info(E.PEAR.concat(E.PEAR).concat(
                ("sendPayment: " + "Submitting transaction with payment operation ... ").concat(E.RED_DOT)));
        final SubmitTransactionResponse payTransactionResponse = server.submitTransaction(paymentTx);
        if (payTransactionResponse.isSuccess()) {
            final String msg = E.LEAF.concat(E.LEAF.concat(E.LEAF)
                    .concat("Payment Transaction is successful. \uD83D\uDE21 Check fiat balances on user account \uD83D\uDE21"));
            LOGGER.info(msg);
            final AccountResponse userAccountResponse = server.accounts().account(destinationAccountId);
            // add payment to database
            final PaymentRequest request = new PaymentRequest();
            request.setAmount(amount);
            request.setAnchorId(anchor.getAnchorId());
            request.setDate(new DateTime().toDateTimeISO().toString());
            request.setAssetCode(assetCode);
            request.setSourceAccount(sourceKeyPair.getAccountId());
            request.setDestinationAccount(destinationAccountId);
            request.setLedger(payTransactionResponse.getLedger());
            request.setPaymentRequestId(UUID.randomUUID().toString());
            firebaseService.addPaymentRequest(request);
            return request;
        } else {
            processPaymentError(payTransactionResponse);
        }
        return null;
    }

    public static void processPaymentError(SubmitTransactionResponse payTransactionResponse) throws Exception {
        if (payTransactionResponse.getDecodedTransactionResult().isPresent()) {
            TransactionResult transactionResult = payTransactionResponse.getDecodedTransactionResult().get();
            PaymentResult paymentResult = null;
            for (OperationResult result : transactionResult.getResult().getResults()) {
                if (result.getTr().getCreateAccountResult() != null) {
                    paymentResult = result.getTr().getPaymentResult();
                }
            }
            if (paymentResult == null) {
                throw new Exception("\uD83C\uDF3C PaymentOperation failed ");
            }
            final String msgx = E.NOT_OK
                    .concat(E.NOT_OK.concat(E.ERROR).concat("Payment Transaction Failed : "));
            switch (paymentResult.getDiscriminant().getValue()) {
                case -1:
                    LOGGER.info(msgx + "PAYMENT_MALFORMED");
                    throw new Exception("PAYMENT_MALFORMED");
                case -2:
                    LOGGER.info(msgx + "PAYMENT_UNDERFUNDED");
                    throw new Exception("PAYMENT_UNDERFUNDED");
                case -3:
                    LOGGER.info(msgx + "PAYMENT_SRC_NO_TRUST");
                    throw new Exception("PAYMENT_SRC_NO_TRUST");
                case -4:
                    LOGGER.info(msgx + "PAYMENT_SRC_NOT_AUTHORIZED");
                    throw new Exception("PAYMENT_SRC_NOT_AUTHORIZED");
                case -5:
                    LOGGER.info(msgx + "PAYMENT_NO_DESTINATION");
                    throw new Exception("PAYMENT_NO_DESTINATION");
                case -6:
                    LOGGER.info(msgx + "PAYMENT_NO_TRUST");
                    throw new Exception("PAYMENT_NO_TRUST");
                case -7:
                    LOGGER.info(msgx + "PAYMENT_NOT_AUTHORIZED");
                    throw new Exception("PAYMENT_NOT_AUTHORIZED");
                case -8:
                    LOGGER.info(msgx + "PAYMENT_LINE_FULL");
                    throw new Exception("PAYMENT_LINE_FULL");
                case -9:
                    LOGGER.info(msgx + "PAYMENT_NO_ISSUER");
                    throw new Exception("PAYMENT_NO_ISSUER");
                default:
                    throw new Exception(msgx + " UNKNOWN ERROR");
            }
        } else {
            String msg = payTransactionResponse.getResultXdr().get();
            LOGGER.info("Error happened in AccountService: " + msg);
            if (msg.contains(PAYMENT_LINE_FULL)) {
                throw new Exception(E.NOT_OK + E.NOT_OK + E.NOT_OK.concat(E.ERROR) + " PAYMENT_LINE_FULL ERROR");
            }
            throw new Exception(E.NOT_OK.concat(E.ERROR) + " UNKNOWN ERROR");
        }
    }

    private static final String PAYMENT_LINE_FULL = "AAAAAAAAAGT/////AAAAAQAAAAAAAAAB////+AAAAAA=";

    @Value("${userXLMStartingBalance}")
    private String userXLMStartingBalance;

    @Value("${userFiatStartingBalance}")
    private String userFiatStartingBalance;
    @Value("${userXLMStartingBalance}")
    private String userFiatLimit;

    public AccountResponseBag addBFNAccount(AccountInfoDTO accountInfo) throws Exception {

        AccountResponseBag bag = createAndFundUserAccount(userXLMStartingBalance, userFiatStartingBalance, userFiatLimit);
        accountInfo.setStellarAccount(bag.getAccountResponse().getAccountId());

        try {
            firebaseService.updateBFNAccount(accountInfo);
        } catch (Exception e) {
            firebaseService.createBFNAccount(accountInfo);
        }


        LOGGER.info(E.COFFEE + E.COFFEE + E.COFFEE +
                "BFN Account now has an Account with the Stellar based Anchor " + E.RED_APPLE);
        return bag;
    }

    @Autowired
    private CryptoService cryptoService;

    /*
     * 🍊 🍊 🍊 Anchors: issuing assets Any account can issue assets on the Stellar
     * network. Entities that issue assets are called anchors. Anchors can be run by
     * individuals, small businesses, local communities, nonprofits, organizations,
     * etc. Any type of financial institution–a bank, a payment processor–can be an
     * anchor.
     *
     * 🍎 Each anchor has an issuing account from which it issues the asset.
     *
     * 🔺🔺🔺🔺🔺 ChangeTrustOperation Possible errors:
     *
     * Error Code Description CHANGE_TRUST_MALFORMED -1 The input to this operation
     * is invalid. CHANGE_TRUST_NO_ISSUER -2 The issuer of the asset cannot be
     * found. CHANGE_TRUST_INVALID_LIMIT -3 The limit is not sufficient to hold the
     * current balance of the trustLine and still satisfy its buying liabilities.
     * CHANGE_TRUST_LOW_RESERVE -4 This account does not have enough XLM to satisfy
     * the minimum XLM reserve increase caused by adding a subEntry and still
     * satisfy its XLM selling liabilities. For every new trustLine added to an
     * account, the minimum reserve of XLM that account must hold increases.
     * CHANGE_TRUST_SELF_NOT_ALLOWED -5 The source account attempted to create a
     * trustLine for itself, which is not allowed.
     */
    public SubmitTransactionResponse changeTrustLine(final String issuingAccount, final String userSeed,
                                                     final String limit, final String assetCode) throws Exception {
        LOGGER.info("\uD83C\uDF40 .......... createTrustLines ........ \uD83C\uDF40 " + " \uD83C\uDF40 code: "
                + assetCode + " \uD83C\uDF40 limit: " + limit + " issuingAccount: " + issuingAccount);
        try {
            setServerAndNetwork();
            final KeyPair userKeyPair = KeyPair.fromSecretSeed(userSeed);
            final Asset asset = Asset.createNonNativeAsset(assetCode, issuingAccount);
            final AccountResponse userAccountResponse = server.accounts().account(userKeyPair.getAccountId());
            final Transaction.Builder transactionBuilder = new Transaction.Builder(userAccountResponse, network);

            transactionBuilder.addOperation(new ChangeTrustOperation.Builder(asset, limit).build());

            transactionBuilder.addMemo(Memo.text("Create Trust Line"));
            transactionBuilder.setBaseFee(100);
            transactionBuilder.setTimeout(360);
            final Transaction transaction = transactionBuilder.build();

            transaction.sign(userKeyPair);

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);

            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  "
                        + "Stellar issueAsset: ChangeTrustOperation has been executed OK: "
                        + "\uD83C\uDF4E \uD83C\uDF4E isSuccess: " + submitTransactionResponse.isSuccess()
                        + " \uD83C\uDF4E assetCode: ".concat(assetCode) + " \uD83C\uDF4E User account: "
                        + userAccountResponse.getAccountId());
            } else {
                TransactionResult transactionResult = submitTransactionResponse.getDecodedTransactionResult().get();
                ChangeTrustResult changeTrustResult = null;
                for (OperationResult result : transactionResult.getResult().getResults()) {
                    if (result.getTr().getCreateAccountResult() != null) {
                        changeTrustResult = result.getTr().getChangeTrustResult();
                    }
                }
                if (changeTrustResult == null) {
                    throw new Exception("ChangeTrustOperation failed");
                }
                final String msgx = E.NOT_OK
                        .concat(E.NOT_OK.concat(E.ERROR).concat("ChangeTrustOperation Failed : "));
                switch (changeTrustResult.getDiscriminant().getValue()) {
                    case -1:
                        LOGGER.info(msgx + "CHANGE_TRUST_MALFORMED");
                        throw new Exception("CHANGE_TRUST_MALFORMED");
                    case -2:
                        LOGGER.info(msgx + "CHANGE_TRUST_NO_ISSUER");
                        throw new Exception("CHANGE_TRUST_NO_ISSUER");
                    case -3:
                        LOGGER.info(msgx + "CHANGE_TRUST_INVALID_LIMIT");
                        throw new Exception("CHANGE_TRUST_INVALID_LIMIT");
                    case -4:
                        LOGGER.info(msgx + "CHANGE_TRUST_LOW_RESERVE");
                        throw new Exception("CHANGE_TRUST_LOW_RESERVE");
                    case -5:
                        LOGGER.info(msgx + "CHANGE_TRUST_SELF_NOT_ALLOWED");
                        throw new Exception("CHANGE_TRUST_SELF_NOT_ALLOWED");

                    default:
                        throw new Exception(msgx + " UNKNOWN ERROR");
                }
            }
            return submitTransactionResponse;
        } catch (final Exception e) {
            throw new Exception("ChangeTrustOperation failed", e);
        }
    }

    private static File TOML_FILE;

    public List<AssetBag> getAnchorCurrencies() throws Exception {
        Anchor anchor = firebaseService.getAnchor();
        return getDefaultAssets(anchor.getIssuingAccount().getAccountId());
    }

    public List<AssetBag> getDefaultAssets(final String issuingAccount) throws Exception {
        final List<AssetBag> mList = new ArrayList<>();
        LOGGER.info(E.PRESCRIPTION.concat(E.PRESCRIPTION)
                + "getDefaultAssets: get stellar.toml file and return to caller...");
        final ClassLoader classLoader = getClass().getClassLoader();
        if (TOML_FILE == null) {
            TOML_FILE = new File(Objects.requireNonNull(classLoader.getResource("_well-known/stellar.toml")).getFile());
        }
        if (TOML_FILE.exists()) {
            LOGGER.info(
                    "\uD83C\uDF3C \uD83C\uDF3C ... stellar.toml File has been found at well-known/stellar.toml: \uD83C\uDF3C \uD83C\uDF3C : "
                            + " issuingAccount: ".concat(issuingAccount).concat(" toml path: ")
                            + TOML_FILE.getAbsolutePath());
            final Toml toml = new Toml().read(TOML_FILE);
            final List<HashMap<String,String>> currencies = toml.getList("CURRENCIES");
            LOGGER.info(
                    "\uD83C\uDF3C \uD83C\uDF3C ... currencies from stellar.toml: \uD83C\uDF3C \uD83C\uDF3C : " + currencies.size());
            for (final HashMap<String,String> currency : currencies) {

                final String code = currency.get("code").toString();
                mList.add(new AssetBag(code, Asset.createNonNativeAsset(code, issuingAccount)));
//
            }
        } else {
            LOGGER.info(" \uD83C\uDF45 stellar.toml : File NOT found. this is where .toml needs to go; " +
                    " \uD83C\uDF45 _well-known/stellar.toml \uD83C\uDF45 ");
            throw new Exception("stellar.toml not found");
        }

        if (mList.isEmpty()) {
            LOGGER.info(E.RED_DOT.concat(E.RED_DOT.concat(E.RED_DOT)
                    .concat("Currencies missing from STELLAR TOML file. Please add issuing account after creation ")));
            mList.add(new AssetBag("ZARK", Asset.createNonNativeAsset("ZARK", issuingAccount)));
//            mList.add(new AssetBag("USD", Asset.createNonNativeAsset("USD", issuingAccount)));
//            mList.add(new AssetBag("GBP", Asset.createNonNativeAsset("GBP", issuingAccount)));
//            mList.add(new AssetBag("EUR", Asset.createNonNativeAsset("EUR", issuingAccount)));

        }

        return mList;
    }

    public static class AssetBag {
        String assetCode;
        Asset asset;

        public AssetBag(final String assetCode, final Asset asset) {
            this.assetCode = assetCode;
            this.asset = asset;
        }

        public String getAssetCode() {

            return assetCode;
        }

        public void setAssetCode(final String assetCode) {
            this.assetCode = assetCode;
        }

        public Asset getAsset() {
            return asset;
        }

        public void setAsset(final Asset asset) {
            this.asset = asset;
        }
    }

    public SubmitTransactionResponse createAsset(final String issuingAccountSeed, final String distributionAccount,
                                                 final String assetCode, final String amount) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "  .......... createAsset ........ \uD83C\uDF40 "
                + " \uD83C\uDF40 code: " + assetCode + " \uD83C\uDF40 " + " amount:" + amount
                + "\n \uD83C\uDF51 issuingAccountSeed: " + issuingAccountSeed + " \uD83C\uDF51 distributionAccount: "
                + distributionAccount);
        try {
            setServerAndNetwork();
            final KeyPair issuingKeyPair = KeyPair.fromSecretSeed(issuingAccountSeed);
            final AssetBag asset = new AssetBag(assetCode,
                    Asset.createNonNativeAsset(assetCode, issuingKeyPair.getAccountId()));

            final AccountResponse issuingAccount = server.accounts().account(issuingKeyPair.getAccountId());
            LOGGER.info("\uD83C\uDF40 Issuing account: " + issuingAccount.getAccountId()
                    + " \uD83C\uDF51 ... create transaction with payment operation ... starting ...");

            final Transaction.Builder trBuilder = new Transaction.Builder(issuingAccount, network);
            trBuilder.addOperation(new PaymentOperation.Builder(distributionAccount, asset.asset, amount)
                    .setSourceAccount(issuingKeyPair.getAccountId()).build());

            trBuilder.addMemo(Memo.text("Fiat Token ".concat(assetCode)));
            trBuilder.setBaseFee(100);
            trBuilder.setTimeout(360);
            final Transaction transaction = trBuilder.build();

            transaction.sign(issuingKeyPair);
            LOGGER.info(
                    "\uD83C\uDF40 PaymentOperation tx has been signed by issuing KeyPair ... \uD83C\uDF51 on to submission ... ");

            final SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(transaction);
            if (submitTransactionResponse.isSuccess()) {
                LOGGER.info("\uD83D\uDC99 \uD83D\uDC99 \uD83D\uDC99  "
                        + "Stellar createAsset: PaymentOperation has been executed OK: \uD83C\uDF4E \uD83C\uDF4E isSuccess: "
                        + submitTransactionResponse.isSuccess());
            } else {
                LOGGER.info(E.NOT_OK + "ERROR: \uD83C\uDF45 resultXdr: " + submitTransactionResponse.getResultXdr().get());
                processPaymentError(submitTransactionResponse);
            }
            return submitTransactionResponse;
        } catch (final Exception e) {
            throw new Exception("PaymentOperation failed", e);
        }
    }

    public void listenForTransactions() throws Exception {
        setServerAndNetwork();
        // todo - what do we want to listen for ???
        LOGGER.info(
                "\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 ... Listen For Stellar Accounts ...");
        server.accounts().stream(new EventListener<AccountResponse>() {
            @Override
            public void onEvent(final AccountResponse accountResponse) {
                LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 Accounts Listener fired, "
                        + "accountResponse received, add to Firestore ...");
                try {
                    String res = firebaseService.addAccountResponse(accountResponse);
                    LOGGER.info(E.COFFEE + E.COFFEE + res);
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, "AccountListener failed", e);
                }
            }

            @Override
            public void onFailure(final Optional<Throwable> optional, final Optional<Integer> optional1) {
                try {
                    LOGGER.info("\uD83C\uDF45 accountListener onFailure: " + optional.get().getMessage());
                    LOGGER.severe("AccountListener failed");
                } catch (final Exception e) {
                    // ignore
                }
            }
        });

        // LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45
        // ... Listen For Stellar Payments ...");
        // SSEStream<OperationResponse> mm = server.payments().stream(new
        // EventListener<OperationResponse>() {
        // @Override
        // public void onEvent(OperationResponse operationResponse) {
        // LOGGER.info("isTransactionSuccessful:
        // ".concat(operationResponse.isTransactionSuccessful().toString()
        // .concat(" SourceAccount: ").concat(operationResponse.getSourceAccount())));
        // try {
        // firebaseService.addOperationResponse(operationResponse);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
        // @Override
        // public void onFailure(Optional<Throwable> optional, Optional<Integer>
        // optional1) {
        // try {
        // LOGGER.info("\uD83C\uDF45 operationResponse onFailure: " +
        // optional.get().getMessage());
        // LOGGER.severe("operationResponse failed");
        // } catch (Exception e) {
        // //ignore
        // }
        // }
        // });
        // LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45
        // ... Listen For Stellar Transactions ...");
        // server.transactions().stream(new EventListener<TransactionResponse>() {
        // @Override
        // public void onEvent(TransactionResponse transactionResponse) {
        // LOGGER.info("\uD83C\uDF4E transactionListener:onEvent:TransactionResponse:
        // isSuccessful: "
        // + transactionResponse.isSuccessful()
        // + " \uD83D\uDC99 source account: " + transactionResponse.getSourceAccount());
        // try {
        // firebaseService.addTransactionResponse(transactionResponse);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
        // @Override
        // public void onFailure(Optional<Throwable> optional, Optional<Integer>
        // optional1) {
        // try {
        // LOGGER.info("\uD83C\uDF45 transactionListener onFailure: " +
        // optional.get().getMessage());
        // LOGGER.severe("transactionListener failed");
        // } catch (Exception e) {
        // //ignore
        // }
        // }
        // });

    }

    public SubmitTransactionResponse setOptions(final String seed, final int clearFlags, final int highThreshold,
                                                final int lowThreshold, final String inflationDestination, final int masterKeyWeight) throws Exception {

        setServerAndNetwork();
        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        final AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());
        final SetOptionsOperation operation = new SetOptionsOperation.Builder()
                .setClearFlags(clearFlags)
                .setHighThreshold(highThreshold)
                .setLowThreshold(lowThreshold)
                .setInflationDestination(inflationDestination)
                .setSourceAccount(keyPair.getAccountId())
                .setMasterKeyWeight(masterKeyWeight)
                .setHomeDomain(domain)
                .build();

        final Transaction transaction = new Transaction.Builder(sourceAccount, network)
                .addOperation(operation)
                .setTimeout(TIMEOUT_IN_SECONDS).setBaseFee(100).build();
        try {
            transaction.sign(keyPair);
            final SubmitTransactionResponse response = server.submitTransaction(transaction);
            LOGGER.info("setOptions: SubmitTransactionResponse: \uD83D\uDC99 Success? : " + response.isSuccess()
                    + " \uD83D\uDC99 ");
            LOGGER.info(
                    response.isSuccess() ? "setOptions transaction is SUCCESSFUL" : "setOptions transaction failed");
            TransactionResult transactionResult = response.getDecodedTransactionResult().get();
            SetOptionsResult setOptionsResult = null;
            for (OperationResult result : transactionResult.getResult().getResults()) {
                if (result.getTr().getCreateAccountResult() != null) {
                    setOptionsResult = result.getTr().getSetOptionsResult();
                }
            }
            if (setOptionsResult == null) {
                throw new Exception("SetOptionsOperation failed");
            }
            final String msgx = E.NOT_OK
                    .concat(E.NOT_OK.concat(E.ERROR).concat("SetOptionsOperation Failed : "));
            switch (setOptionsResult.getDiscriminant().getValue()) {
                case -1:
                    LOGGER.info(msgx + "SET_OPTIONS_LOW_RESERVE");
                    throw new Exception("SET_OPTIONS_LOW_RESERVE");
                case -2:
                    LOGGER.info(msgx + "SET_OPTIONS_TOO_MANY_SIGNERS");
                    throw new Exception("SET_OPTIONS_TOO_MANY_SIGNERS");
                case -3:
                    LOGGER.info(msgx + "SET_OPTIONS_BAD_FLAGS");
                    throw new Exception("SET_OPTIONS_BAD_FLAGS");
                case -4:
                    LOGGER.info(msgx + "SET_OPTIONS_INVALID_INFLATION");
                    throw new Exception("SET_OPTIONS_INVALID_INFLATION");
                case -5:
                    LOGGER.info(msgx + "SET_OPTIONS_CANT_CHANGE");
                    throw new Exception("SET_OPTIONS_CANT_CHANGE");

                default:
                    return response;
            }

        } catch (final Exception e) {
            final String msg = "Failed to setOptions: ";
            LOGGER.severe(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }

    @Autowired
    AnchorSep10Challenge anchorSep10Challenge;

    public String handleChallenge(final String seed) throws Exception {

        setServerAndNetwork();
        final KeyPair keyPair = KeyPair.fromSecretSeed(seed);
        //todo - finish this ...
        final AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());


        return null;

    }

    private void setServerAndNetwork() {
        if (status == null) {
            LOGGER.info("\uD83D\uDE08 \uD83D\uDC7F Set status to dev because status is NULL");
            status = "dev";
        }
        if (server != null) return;
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

    public Server getServer() {
        setServerAndNetwork();
        return server;
    }
}
