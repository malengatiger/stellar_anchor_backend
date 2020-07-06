package com.anchor.api.controllers;

import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.anchor.AnchorBag;
import com.anchor.api.data.anchor.Client;
import com.anchor.api.data.info.Info;
import com.anchor.api.services.*;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.Emoji;
import com.anchor.api.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stellar.sdk.responses.AccountResponse;
import shadow.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class AnchorController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public AnchorController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C AnchorController  " +
                "\uD83C\uDF51 constructed and ready to go! \uD83C\uDF45 CORS enabled for the controller");
    }

    @Autowired
    private ApplicationContext context;
    @Autowired
    private AnchorAccountService anchorAccountService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private DemoDataGenerator demoDataGenerator;
    @Value("${status}")
    private String status;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        LOGGER.info(em + " StellarAnchorApplication / ...");
        return "\uD83D\uDC99 \uD83D\uDC9C StellarAnchorApplication up and running ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }

    @Autowired
    private TOMLService tomlService;

    /**
     * Upload the Anchor properties in anchor.toml nee stellar. This file has private properties
     * that the Anchor needs for proper operation
     * @param anchorId anchor identifier
     * @param multipartFile  toml file with anchor properties
     * @return byte[] - bytes from the file
     * @throws Exception upload failed
     */
    @PostMapping(value = "/uploadAnchorTOML", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] uploadAnchorTOML(@RequestParam("anchorId") String anchorId,
                             @RequestParam("file") MultipartFile multipartFile) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:uploadAnchorTOML...");
        byte[] bytes = multipartFile.getBytes();
        File mFile = new File("file_" + System.currentTimeMillis());
        Path path = Paths.get(mFile.getAbsolutePath());
        Files.write(path, bytes);
        LOGGER.info("....... multipart TOML file received: \uD83C\uDFBD "
                .concat(" length: " + mFile.length() + " bytes"));
        tomlService.encryptAndUploadAnchorFile(anchorId, mFile);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return bytes;
    }
    /**
     * Upload the Anchor properties in anchor.toml nee stellar. This file has private properties
     * that the Anchor needs for proper operation
     * @param anchorId anchor identifier
     * @param multipartFile  toml file with anchor properties
     * @return byte[] - bytes from the file
     * @throws Exception upload failed
     */
    @PostMapping(value = "/uploadStellarTOML", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] uploadStellarTOML(@RequestParam("anchorId") String anchorId,
                             @RequestParam("file") MultipartFile multipartFile) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:uploadStellarTOML...");
        byte[] bytes = multipartFile.getBytes();
        File mFile = new File("file_" + System.currentTimeMillis());
        Path path = Paths.get(mFile.getAbsolutePath());
        Files.write(path, bytes);
        LOGGER.info("....... multipart StellarTOML file received: \uD83C\uDFBD "
                .concat(" length: " + mFile.length() + " bytes"));
        tomlService.encryptAndUploadStellarFile(anchorId, mFile);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned from upload .... OK!");
        return bytes;
    }

    /*
        Get the Anchor's properties from anchor.toml
     */
    @GetMapping(value = "/getAnchorTOML", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAnchorTOML(@RequestParam("anchorId") String anchorId) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:getAnchorTOML...");
        Toml toml = tomlService.getAnchorToml(anchorId);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned TOML from download .... "
                .concat(" databaseUrl: ")
                .concat(toml.getString("databaseUrl")));
        return toml.toMap();
    }
    @GetMapping(value = "/getStellarTOML", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getStellarTOML(@RequestParam("anchorId") String anchorId) throws Exception {

        LOGGER.info(Emoji.RAIN_DROPS.concat(Emoji.RAIN_DROPS) + "AnchorController:getStellarTOML...");
        Toml toml = tomlService.getStellarToml(anchorId);
        LOGGER.info("\uD83C\uDFBD \uD83C\uDFBD \uD83C\uDFBD Returned TOML from download .... "
        .concat(toml.toMap().toString()));
        return toml.toMap();
    }


    @GetMapping(value = "/.well-known/stellar.toml", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Map<String, Object> getStellarToml() throws Exception {
        LOGGER.info(em + " get stellar.toml file and return to caller...");
        List<Anchor> anchors = firebaseService.getAnchors();
        if (!anchors.isEmpty()) {
            Anchor anchor = anchors.get(0);
            Toml toml = tomlService.getStellarToml(anchor.getAnchorId());
            return toml.toMap();
        } else {
            throw new Exception("TOML file not found");
        }
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> ping() throws Exception {
        LOGGER.info(em + " Pinging StellarAnchorApplication and getting anchors...");
        List<Anchor> anchors = context.getBean(FirebaseService.class).getAnchors();
        List<String > mList = new ArrayList<>();
        int cnt = 0;
        for (Anchor anchor : anchors) {
            cnt++;
            mList.add(anchor.getName());
        }
        LOGGER.info( em + "  StellarAnchorApplication pinged at "
                + new Date().toString() + em + "  anchors found: " + anchors.size());
        return mList;
    }

    @GetMapping(value = "/getAccountUsingSeed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Balances getAccountUsingSeed(@RequestParam String seed) throws Exception {
        LOGGER.info(em+" AnchorController:getAccountUsingSeed ...");
        AccountResponse response = accountService.getAccountUsingSeed(seed);
        LOGGER.info( em+" AnchorController getAccount returned: "
                + response.getAccountId() + Emoji.LEAF);
        AccountResponse.Balance[] balances = response.getBalances();
        List<AccountResponse.Balance> balanceList = new ArrayList<>();
        Collections.addAll(balanceList, balances);

        Balances mBalances = new Balances(balanceList, response.getAccountId(), response.getSequenceNumber(),
                new DateTime().toDateTimeISO().toString());
        firebaseService.addBalances(mBalances);
        LOGGER.info(Emoji.PEPPER.concat(Emoji.PEPPER) + "Returning balances " + G.toJson(mBalances));
        return mBalances;
    }

   
    @GetMapping(value = "/getAccountUsingAccountId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Balances getAccountUsingAccountId(@RequestParam String accountId) throws Exception {
        LOGGER.info(em + " AnchorController:getAccountUsingAccountId ...");
        AccountResponse response = accountService.getAccountUsingAccount(accountId);
        LOGGER.info( Emoji.LEAF.concat(Emoji.LEAF) +" AnchorController getAccount returned: "
                + response.getAccountId() + Emoji.LEAF);
        AccountResponse.Balance[] balances = response.getBalances();
        List<AccountResponse.Balance> balanceList = new ArrayList<>();
        Collections.addAll(balanceList, balances);
        Balances mBalances = new Balances(balanceList, response.getAccountId(), response.getSequenceNumber(),
                new DateTime().toDateTimeISO().toString());
        firebaseService.addBalances(mBalances);
        LOGGER.info(Emoji.PEPPER.concat(Emoji.PEPPER) + "Returning balances " + G.toJson(mBalances));
        return mBalances;
    }


    @GetMapping(value = "/getAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor getAnchor(@RequestParam String anchorId) throws Exception {
        LOGGER.info(em + " AnchorController:getAnchor ..."
        .concat(anchorId));
        Anchor response = firebaseService.getAnchor(anchorId);
        LOGGER.info( Emoji.LEAF.concat(Emoji.LEAF) + " AnchorController getAnchor returned: "
                + response.getName());

        return response;
    }

    @Autowired
    private FirebaseService firebaseService;
    @GetMapping(value = "/getAnchorClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getClients(@RequestParam String anchorId) throws Exception {
        return firebaseService.getAnchorClients(anchorId);
    }

    @PostMapping(value = "/createAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor createAnchor(@RequestBody AnchorBag anchorBag) throws Exception {
        LOGGER.info(em + " AnchorController:createAnchor ...");
        if (anchorBag.getFundingSeed() == null) {
            throw new Exception("Funding Account Seed missing");
        }
        Anchor anchor = anchorAccountService.createAnchorAccounts(
                anchorBag.getAnchor(),
                anchorBag.getPassword(),
                anchorBag.getAssetAmount(),
                anchorBag.getFundingSeed(),
                anchorBag.getStartingBalance());

        LOGGER.info(Emoji.LEAF + " AnchorAccountService returns Anchor: \uD83C\uDF4E "
                + anchor.getName() + "  \uD83C\uDF4E anchorId: " + anchor.getAnchorId());
        LOGGER.info(Emoji.LEAF +Emoji.LEAF +Emoji.LEAF +Emoji.LEAF +
                " ANCHOR CREATED: ".concat(G.toJson(anchor)));
        return anchor;
    }

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/fundAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public AgentFundingRequest fundAgent(@RequestBody AgentFundingRequest fundingRequest) throws Exception {
        LOGGER.info(em + "AnchorController: fundAgent requested .... " + G.toJson(fundingRequest));
        fundingRequest.setAgentFundingRequestId(UUID.randomUUID().toString());
        fundingRequest = paymentService.fundAgent(fundingRequest);
        String msg = Emoji.LEAF + "Agent Funding complete: " + fundingRequest.getAgentFundingRequestId();
        LOGGER.info(Emoji.LEAF.concat(msg));
        return fundingRequest;
    }

    @GetMapping("/createKeyRing")
    public String createKeyRing(@RequestParam String keyRingId) throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "StellarAnchorApplication: createKeyRing ... ... ...");
        String keyRing = cryptoService.createKeyRing();
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + " createKeyRing done!: \uD83C\uDF4E "
        .concat(keyRing));
        return keyRing;
    }
    @GetMapping("/createCryptoKey")
    public String createCryptoKey() throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "StellarAnchorApplication: createCryptoKey ... ... ...");
        String cryptoKey = cryptoService.createCryptoKey();
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "createCryptoKey done!: \uD83C\uDF4E "
                .concat(cryptoKey));
        return cryptoKey;
    }
    @PostMapping(value = "/sendPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public PaymentRequest sendPayment(PaymentRequest paymentRequest) throws Exception {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) +
            "............. send payment ... ".concat(G.toJson(paymentRequest)));
        PaymentRequest response = paymentService.sendPayment(paymentRequest);
        LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF) + "Payment has been successfully sent: ".concat(G.toJson(response)));
        return response;
    }

    @GetMapping("/createTestInfo")
    public Info createTestInfo() {
        LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + " AnchorController:createTestInfo ...");
        return Util.createTestInfo();
    }
    private static final String em ="\uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E ";
    public static class Balances {
        List<AccountResponse.Balance> balances;
        String account, date;
        private Long sequenceNumber;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Balances(List<AccountResponse.Balance> balances, String account, Long sequenceNumber, String date) {
            this.balances = balances;
            this.account = account;
            this.date = date;
            this.sequenceNumber = sequenceNumber;
        }

        public List<AccountResponse.Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<AccountResponse.Balance> balances) {
            this.balances = balances;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public Long getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(Long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }

}
