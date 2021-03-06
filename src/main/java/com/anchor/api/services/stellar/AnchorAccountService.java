package com.anchor.api.services.stellar;

import com.anchor.api.data.account.Account;
import com.anchor.api.data.account.AccountResponseBag;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.data.anchor.AnchorUser;
import com.anchor.api.services.misc.CryptoService;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.util.E;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

//Anchor Stellar Account: GBD552IFC6M3C7NYAMIIG6S3KLLKQV6Y2VKPVTVFGRPR5KXQUZV4UIM7
//SCPLI2HYKCEIDVUJJIBMCKIORDTEK7FA5SKSBIYQYJPYZIY5JQ5PKZ47
@Service
public class AnchorAccountService {
    public static final Logger LOGGER = Logger.getLogger(AnchorAccountService.class.getSimpleName());
    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private ApplicationContext context;

    @Value("${status}")
    private String status;

    @Value("${sendgrid}")
    private String sendgridAPIKey;

    @Value("${fromMail}")
    private String fromMail;

    @Value("${limit}")
    private String limit;

    @Value("${anchorStartingBalance}")
    private String anchorStartingBalance;
    @Autowired
    private AccountService accountService;
    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private CryptoService cryptoService;
//🍀 🍀
    public AnchorAccountService() {
        LOGGER.info(E.DRUM + E.DRUM + "AnchorAccountService Constructor fired ..." +
                E.HEART_ORANGE + "manages the setup of Anchor base and issuing accounts");
    }

    public Anchor assembleAnchor(AnchorBag anchorBag) throws Exception {
        if (anchorBag.getFundingSeed() == null) {
            throw new Exception("Funding Account Seed missing");
        }
        if (anchorBag.getAnchor() == null) {
            throw new Exception("Anchor missing");
        }
        if (anchorBag.getAssetAmount() == null) {
            throw new Exception("Asset Amount missing");
        }
        if (anchorBag.getStartingBalance() == null) {
            throw new Exception("StartingBalance  missing");
        }
        LOGGER.info( " ,,,,,, AnchorController:createAnchor started by DemoDataController :" +
                " anchorAccountService.createAnchorAccounts starting ,,,,,,,,,,,,,," );
        Anchor anchor = createAnchorAccounts(
                anchorBag.getAnchor(),
                anchorBag.getPassword(),
                anchorBag.getAssetAmount(),
                anchorBag.getFundingSeed(),
                anchorBag.getStartingBalance(), anchorBag.getDistributionBalance());

        LOGGER.info(E.LEAF + " AnchorAccountService returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId());
        LOGGER.info(E.LEAF + E.LEAF + E.LEAF + E.LEAF +
                " ANCHOR CREATED");
        LOGGER.info(G.toJson(anchor));
        return anchor;
    }

    public Anchor createAnchorAccounts(Anchor mAnchor, String password,
                                       String assetAmount, String fundingSeed,
                                       String startingBalance, String distributionBalance)
            throws Exception {
        LOGGER.info(E.FERN + E.FERN + "AnchorAccountService: creating Anchor Accounts " +
                ".... \uD83C\uDF40 DEV STATUS: " + status + " \uD83C\uDF51 " +
                "startingBalance: " + startingBalance + " \uD83C\uDF51 seed: " + fundingSeed);
        Anchor anchor = new Anchor();

        DateTime dateTime = new DateTime();
        anchor.setDate(dateTime.toDateTimeISO().toString());
        anchor.setName(mAnchor.getName());
        anchor.setEmail(mAnchor.getEmail());
        anchor.setCellphone(mAnchor.getCellphone());
        anchor.setAnchorId(UUID.randomUUID().toString());

        AccountResponseBag baseAccount = null;
        AccountResponseBag distributionAccount = null;
        AccountResponseBag issuingAccount = null;
        try {
            baseAccount = accountService.createAndFundAnchorAccount(
                    fundingSeed, startingBalance);
             distributionAccount = accountService.createAndFundAnchorAccount(
                    baseAccount.getSecretSeed(), anchorStartingBalance);
             issuingAccount = accountService.createAndFundAnchorAccount(
                    baseAccount.getSecretSeed(), anchorStartingBalance);
        } catch (Exception e) {
            String err = "\uD83D\uDC7F \uD83D\uDE21";
            String msg = err + "The Anchor set of Stellar accounts failed to complete creation.  " + err;
            if (baseAccount == null) {
                msg += " Base Account failed to create ";
            }
            if (distributionAccount == null) {
                msg += "  \uD83D\uDE21 Distribution Account failed to create ";
            }
            if (issuingAccount == null) {
                msg += "  \uD83D\uDE21 IssuingAccount Account failed to create ";
            }
            LOGGER.severe(msg);
            e.printStackTrace();
            throw e;
        }

        Account base = new Account();
        base.setAccountId(baseAccount.getAccountResponse().getAccountId());
        base.setName("Base Account");
        base.setDate(new DateTime().toDateTimeISO().toString());

        Account issuing = new Account();
        issuing.setAccountId(issuingAccount.getAccountResponse().getAccountId());
        issuing.setDate(new DateTime().toDateTimeISO().toString());
        issuing.setName("Issuing Account");

        Account distribution = new Account();
        distribution.setAccountId(distributionAccount.getAccountResponse().getAccountId());
        distribution.setDate(new DateTime().toDateTimeISO().toString());
        distribution.setName("Distribution Account");

        anchor.setBaseAccount(base);
        anchor.setIssuingAccount(issuing);
        anchor.setDistributionAccount(distribution);

        encryptAndUploadSeedFile(baseAccount.getAccountResponse().getAccountId(),
                baseAccount.getSecretSeed());

        encryptAndUploadSeedFile(issuingAccount.getAccountResponse().getAccountId(),
                issuingAccount.getSecretSeed());

        encryptAndUploadSeedFile(distributionAccount.getAccountResponse().getAccountId(),
                distributionAccount.getSecretSeed());

        try {
            List< AccountService.AssetBag > assets = accountService.getDefaultAssets(
                    issuingAccount.getAccountResponse().getAccountId());
            // Create trustLines for all asset types
            for (AccountService.AssetBag assetBag : assets) {
                SubmitTransactionResponse createTrustResponse = accountService.changeTrustLine(
                        issuingAccount.getAccountResponse().getAccountId(),
                        distributionAccount.getSecretSeed(),
                        limit, assetBag.assetCode);

                LOGGER.info(E.FLOWER_RED + E.FLOWER_RED + "AnchorAccountService: createTrustLine for asset: " + assetBag.assetCode +
                        ".... "+ E.HAPPY+" TrustLine Response isSuccess:  " + createTrustResponse.isSuccess());
                if (createTrustResponse.isSuccess()) {
                    LOGGER.info(E.LEAF.concat(E.LEAF).concat("TrustLine created OK: "
                            + assetBag.assetCode).concat(" " + E.RED_APPLE));
                } else {
                    LOGGER.info(E.ERROR.concat(E.ERROR).concat("TrustLine failed to create: "
                            + assetBag.assetCode).concat(" please tell someone, motherfucker! " + E.RED_APPLE));
                }
            }

            for (AccountService.AssetBag assetBag : assets) {
                LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD).concat(E.YELLOW_BIRD) +
                        "Creating Asset .... ".concat(assetBag.assetCode)
                                .concat(" with assetAmount: ".concat(assetAmount)));
                SubmitTransactionResponse createAssetResponse = accountService.createAsset(
                        issuingAccount.getSecretSeed(),
                        distributionAccount.getAccountResponse().getAccountId(),
                        assetBag.assetCode, assetAmount);

                LOGGER.info(E.HASH.concat(E.HAND2)
                        .concat(" Asset " + assetBag.assetCode + " \uD83C\uDF4E created? "
                                + createAssetResponse.isSuccess())
                .concat(" asset amount: ").concat(assetAmount));
                if (createAssetResponse.isSuccess()) {
                    LOGGER.info(E.LEAF.concat(E.LEAF).concat("Anchor Asset created OK: " + assetBag.assetCode).concat(" " + E.RED_APPLE));
                } else {
                    LOGGER.info(E.ERROR.concat(E.ERROR).concat("Anchor Asset failed to create: " + assetBag.assetCode).concat(" please tell someone, Ben! " + E.RED_APPLE));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(E.NOT_OK +E.NOT_OK +E.NOT_OK + "TrustLine/Asset creation failed" + E.ERROR);
            throw e;
        }

        LOGGER.info(E.LEAF + E.LEAF + E.LEAF +
                " ............... Anchor created and will be added to Firestore: " +
                " " + anchor.getName());
        AnchorUser anchorUser = createAnchorUser(anchor, password);
        anchor.setAnchorUser(anchorUser);
        String res = firebaseService.addAnchor(anchor);
        LOGGER.info(E.COFFEE+E.COFFEE+res);
        LOGGER.info(G.toJson(anchor));
        //todo - send email to confirm the anchor with link ...
        try {
            sendEmail(anchor);
            LOGGER.info(E.FLOWER_YELLOW + E.FLOWER_YELLOW + "Email has been sent ... ");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Email sending failed");
        }

        return anchor;
    }

    private void encryptAndUploadSeedFile(String accountId, String seed) throws IOException {
        //todo - have to check if keyRing etc. exists .....
        LOGGER.info("................ ♦️ ♦️ encryptAndUploadSeedFile ♦️ ♦️ ................");
        try {
            cryptoService.createKeyRing();
        } catch (Exception e) {
            LOGGER.severe("cryptoService.createKeyRing Failed: " + e.getMessage());
        }
        try {
            cryptoService.createCryptoKey();
        } catch (Exception e) {
            LOGGER.severe(" cryptoService.createCryptoKey Failed: " + e.getMessage());
        }
        try {
            cryptoService.encrypt(accountId, seed);
        } catch (Exception e) {
            LOGGER.severe("cryptoService.encrypt Failed: " + e.getMessage());
        }
    }

    private AnchorUser createAnchorUser(Anchor anchor, String password) throws Exception {

        FirebaseService scaffold = context.getBean(FirebaseService.class);

        UserRecord userRecord = scaffold.createUser(anchor.getName(),anchor.getEmail(),password);
        AnchorUser anchorUser = new AnchorUser();
        anchorUser.setFirstName(anchor.getName());
        anchorUser.setEmail(anchor.getEmail());
        anchorUser.setCellphone(anchor.getCellphone());
        anchorUser.setUserId(userRecord.getUid());
        anchorUser.setAnchorId(anchor.getAnchorId());
        DateTime dateTime = new DateTime();
        anchorUser.setDate(dateTime.toDateTimeISO().toString());
        anchorUser.setActive(true);
        String res = firebaseService.addAnchorUser(anchorUser);

        LOGGER.info(E.COFFEE+E.COFFEE+res);
        return anchorUser;
    }


    private void sendEmail(Anchor anchor) throws IOException {

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Sending registration email to user: " + anchor.getEmail());

        //todo - finish registration email composition, links and all, html etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Hi Anchor Admin,\n");
        sb.append("Welcome to The Anchor Network\n");
        sb.append("Click on this link to complete the registration\n");
        sb.append("\nRegards,\n");
        sb.append("Anchor Network Team");

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: send mail from: " + fromMail);

        Email from = new Email(fromMail);
        String subject = "Welcome to " + anchor.getName() + " registration";
        Email to = new Email(anchor.getEmail());
        Content content = new Content("text/plain", sb.toString());
        Mail mail = new Mail(from, subject, to, content);

        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid apiKey: " + sendgridAPIKey);
        SendGrid sg = new SendGrid(sendgridAPIKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C Registration email sent to Anchor user: \uD83E\uDD66  " + anchor.getName() +
                    " \uD83E\uDD66 status code: " + response.getStatusCode());
            LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C SendGrid: " +
                    " response headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw ex;
        }

    }
}
