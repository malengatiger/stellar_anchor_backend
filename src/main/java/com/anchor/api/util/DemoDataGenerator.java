package com.anchor.api.util;

import com.anchor.api.controllers.stellar.AnchorController;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.models.NetworkOperatorDTO;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.data.transfer.sep9.OrganizationKYCFields;
import com.anchor.api.data.transfer.sep9.PersonalKYCFields;
import com.anchor.api.services.misc.CryptoService;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.services.misc.TOMLService;
import com.anchor.api.services.payments.StellarPaymentService;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.stellar.AgentService;
import com.anchor.api.services.stellar.AnchorAccountService;
import com.anchor.api.services.stellar.StokvelService;
import com.bfn.client.data.TradeMatrixItemDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.AccountResponse;

import java.util.*;
import java.util.logging.Logger;

@Service
public class DemoDataGenerator {
    public static final Logger LOGGER = Logger.getLogger(DemoDataGenerator.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public DemoDataGenerator() {
        LOGGER.info(E.RED_CAR.concat(E.RED_CAR) + "Demo data DemoDataGenerator ready and able!");
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
    private FirebaseService firebaseService;
    @Value("${status}")
    private String status;

    @Value("${basePassword}")
    private String basePassword;

    Anchor anchor;
    //todo - document this shit somewhere ....
    /*
    🛎🛎🛎 this is the BEGINNING OF ALL WISDOM ..... 🛎 🛎 🛎
        Steps to recreate demo AnchorBank environment:
            0. Obtain funding seed from Stellar Friend in Lab
            1. Run startAnchor - store anchorId in anchor.toml
            2. Run uploadTOML - upload and encrypt anchor.toml
            3. Run startGeneration with the new anchorId
    🛎🛎🛎
     */

    @Autowired
    private NetworkUtil networkUtil;
    @Value("${bfnUrl}")
    private String bfnUrl;
    @Value("${bfnCustomerUrl}")
    private String bfnCustomerUrl;
    private String seed;

    String concat = E.FERN.concat(E.FIRE.concat(E.FIRE.concat(E.FIRE)));

    public Anchor createNewAnchor(String anchorName, String fundingSeed) throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(E.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }

        LOGGER.info(concat
                + "Start createNewAnchor .... createNetworkOperator  \uD83D\uDECE AND STELLAR.TOML for " + anchorName);
        seed = fundingSeed;
        deleteFirebaseArtifacts();
        LOGGER.info(concat
                + "Firebase collections and users have been deleted for \uD83D\uDD06 " + anchorName);
        Anchor mAnchor = addAnchor(anchorName);
        if (mAnchor == null) {
            String msg = "\uD83D\uDD06 \uD83D\uDD06 \uD83D\uDD06 createNewAnchor: Anchor missing; is NULL";
            LOGGER.info(" \uD83C\uDF40  \uD83C\uDF40 " + msg);
            throw new Exception(msg);
        }
        LOGGER.info(concat
                + "Start Anchor complete. Complete generation after copying anchorId to anchor.toml " +
                "\uD83D\uDECE AND updating STELLAR.TOML. Will start creating NetworkOperator");
        LOGGER.info(E.RED_APPLE + E.RED_APPLE + G.toJson(mAnchor));

        return mAnchor;
    }


    public void startGeneration() throws Exception {
        if (!status.equalsIgnoreCase("dev")) {
            throw new Exception(E.NOT_OK + "Demo Data Generation may not be run in PRODUCTION");
        }
        Anchor mAnchor = firebaseService.getAnchor();
        String concat = E.HEART_BLUE.concat(E.HEART_BLUE).concat(E.HEART_BLUE).concat(E.HEART_BLUE);
        LOGGER.info(concat
                + "........ Anchor:  ........"
                .concat(G.toJson(mAnchor)).concat(" ")
                .concat(E.HEART_BLUE.concat(E.HEART_BLUE)));
        if (mAnchor == null) {
            throw new Exception(E.NOT_OK + E.NOT_OK + "Generator: Anchor is missing from Firestore " + E.NOT_OK + E.NOT_OK);
        }
        Toml toml = tomlService.getAnchorToml();
        if (toml == null) {
            throw new Exception(E.NOT_OK + E.NOT_OK + "Generator: Anchor TOML file is missing: ");
        }
        String anchorId = mAnchor.getAnchorId();
        if (anchorId == null) {
            throw new Exception(E.NOT_OK + "anchorId is NULL");
        }

        LOGGER.info("\n\n" + concat
                + "........ Start Demo Data Generation : Agents and Clients ........".concat(E.HEART_BLUE.concat(E.HEART_BLUE)));

        addAgents(mAnchor);
        generateAgentClients(3, mAnchor);

        LOGGER.info("\n\n\n"
                .concat(E.PRETZEL.concat(E.PRETZEL.concat(E.PRETZEL).concat(E.ALIEN.concat(E.ALIEN))
                        .concat(" ......... starting Agent funding, LoanApplications generation "
                                + " \uD83C\uDF3C \uD83C\uDF3C "))));
        generateAgentFunding();
        generateLoanApplications();

        // LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
        // .concat(Emoji.PANDA.concat(Emoji.PANDA))
        // .concat(" ......... LoanApplication approvals " +
        // "and payments to clients ... \uD83C\uDF3C \uD83C\uDF3C "))));
        // generateLoanApprovals(anchor.getAnchorId());
        //
        // LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
        // .concat(Emoji.BUTTERFLY.concat(Emoji.BUTTERFLY))
        // .concat(" ......... starting LoanPayment settling ... Clients paying back the
        // loans " +
        // " \uD83C\uDF3C \uD83C\uDF3C "))));
        // generatePayments(anchor.getAnchorId());

        // for testing
        // todo - retrieve data for overall status of anchor ...
        // ... (anchor, agent, client dashboard basics here ....)

        // stokvel shit
        // LOGGER.info("\n\n\n".concat(Emoji.PRETZEL.concat(Emoji.PRETZEL.concat(Emoji.PRETZEL)
        // .concat(" ......... starting STOKVEL data generation .... \uD83C\uDF3C
        // \uD83C\uDF3C ")
        // .concat(Emoji.WARNING))));
        // Stokvel stokvel = generateStokvel(anchor.getAnchorId());
        // generateStokvelMembers(stokvel.getStokvelId(), anchor.getAnchorId());

    }

    private void setAnchor() throws Exception {
        if (anchor != null) {
            return;
        }
        Toml toml = tomlService.getAnchorToml();
        if (toml == null) {
            throw new Exception("anchor.toml has not been found. upload the file from your computer");
        } else {
            anchor = firebaseService.getAnchor();
        }

    }

    public void generatePayments() throws Exception {
        LOGGER.info(E.STAR.concat(E.STAR.concat(E.STAR.concat(E.STAR.concat(E.STAR)
                .concat(".......... Generating client payments for all loans until the bitter end !..... ")
                .concat(E.HAND2.concat(E.HAND2))))));
        setAnchor();
        List<Agent> agents = firebaseService.getAgents();
        DateTime now = new DateTime();
        long day = 1000 * 60 * 60 * 24;
        for (Agent agent : agents) {
            List<LoanApplication> list = firebaseService.getAgentLoans(agent.getAgentId());
            Collections.sort(list, Comparator.comparing(LoanApplication::getDate));
            for (LoanApplication application : list) {
                DateTime mDate = DateTime.parse(application.getLastDatePaid());
                if (now.getMillis() - mDate.getMillis() < day) {
                    continue;
                }
                if (application.isApprovedByAgent() && application.isApprovedByClient()) {
                    if (!application.isPaid()) {
                        if (application.getLoanPeriodInMonths() > 0) {
                            generateMonthlyPayments(application);
                        }
                        if (application.getLoanPeriodInWeeks() > 0) {
                            generateWeeklyPayments(application);
                        }
                    }
                }
            }
        }

    }

    private void generateMonthlyPayments(LoanApplication application) throws Exception {
        // List<LoanPayment> payments = firebaseService.getLoanPayments(
        // application.getLoanId());
        int numberOfPayments = application.getLoanPeriodInMonths();
        Agent agent = firebaseService.getAgent(application.getAgentId());
        Client client = firebaseService.getClientById(application.getClientId());
        String clientSeed = cryptoService.getDecryptedSeed(client.getStellarAccountId());
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < numberOfPayments; i++) {
            LoanPayment payment = new LoanPayment();
            payment.setAgentAccount(agent.getStellarAccountId());
            payment.setClientSeed(clientSeed);
            payment.setAgentId(client.getAgentIds().get(0));
            payment.setClientId(client.getClientId());
            payment.setAnchorId(client.getAnchorId());
            payment.setAssetCode(application.getAssetCode());
            payment.setAmount(application.getMonthlyPayment());
            payment.setLoanId(application.getLoanId());
            // calculate next monthly date
            DateTime dateTime1 = DateTime.parse(application.getDate());
            cal.set(dateTime1.getYear(), dateTime1.getMonthOfYear(), dateTime1.getDayOfMonth());
            int lastDate = cal.getActualMaximum(Calendar.DATE);
            DateTime dateTime = dateTime1.plusMonths(i + 1);
            payment.setDate(dateTime.toDateTimeISO().toString());

            try {
                sendPaymentAndSaveOnFuckingDatabase(application, client, clientSeed, payment);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(e.getMessage());

            }

        }
    }

    private void sendPaymentAndSaveOnFuckingDatabase(LoanApplication application, Client client, String clientSeed,
                                                     LoanPayment payment) throws Exception {

        if (!isWithinBalance(payment)) {
            return;
        }
        PaymentRequest request = new PaymentRequest();
        request.setSeed(clientSeed);
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setAmount(payment.getAmount());
        request.setAnchorId(anchor.getAnchorId());
        request.setAssetCode(application.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        request.setDestinationAccount(payment.getAgentAccount());

        PaymentRequest response = stellarPaymentService.sendPayment(request);

        payment.setLedger(response.getLedger());
        payment.setOnTime(true);
        try {
            payment = agentService.addLoanPayment(payment);
            payment.setPaymentRequestId(request.getPaymentRequestId());
            LOGGER.info(E.PRETZEL.concat(E.PRETZEL.concat(E.HAPPY))
                    + "MONTHLY LoanPayment made on Stellar and stored in database; getPaymentRequestId: "
                    .concat(payment.getPaymentRequestId())
                    .concat(" " + E.RED_TRIANGLE.concat(E.RED_TRIANGLE)));
        } catch (Exception e) {
            // ignore
            if (e.getMessage() == null) {
                LOGGER.info(E.ERROR.concat("Unknown error"));
                e.printStackTrace();
            } else {
                LOGGER.info(E.ERROR.concat(e.getMessage()));
            }

        }

    }

    private void generateWeeklyPayments(LoanApplication application) throws Exception {

        int numberOfPayments = application.getLoanPeriodInWeeks();
        Agent agent = firebaseService.getAgent(application.getAgentId());
        Client client = firebaseService.getClientById(application.getClientId());
        String clientSeed = cryptoService.getDecryptedSeed(client.getStellarAccountId());
        for (int i = 0; i < numberOfPayments; i++) {
            LoanPayment payment = new LoanPayment();
            payment.setAgentAccount(agent.getStellarAccountId());
            payment.setClientSeed(clientSeed);
            payment.setAgentId(client.getAgentIds().get(0));
            payment.setClientId(client.getClientId());
            payment.setAnchorId(client.getAnchorId());
            payment.setAssetCode(application.getAssetCode());
            payment.setAmount(application.getWeeklyPayment());
            payment.setLoanId(application.getLoanId());
            // calculate next weekly date
            DateTime dateTime1 = DateTime.parse(application.getDate());
            DateTime dateTime = dateTime1.plusDays((i + 1) * 7);
            payment.setDate(dateTime.toDateTimeISO().toString());

            try {
                sendAndSave(application, client, clientSeed, payment);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info(E.PIG.concat(E.PIG.concat(E.PIG))
                        .concat("This WEEKLY payment failed ".concat(e.getMessage() == null ? "" : e.getMessage())));
                break;
            }

        }
    }

    private boolean isWithinBalance(LoanPayment loanPayment) throws Exception {
        // todo - check account balance for this asset before attempting payment
        AccountResponse accountResponse = accountService.getAccountUsingSeed(loanPayment.getClientSeed());
        AccountResponse.Balance balance = null;
        try {
            for (AccountResponse.Balance bal : accountResponse.getBalances()) {
                if (!bal.getAssetType().equalsIgnoreCase("native")) {
                    if (bal.getAssetCode().equalsIgnoreCase(loanPayment.getAssetCode())) {
                        balance = bal;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (balance == null) {
            throw new Exception("Asset Balance is missing");
        }
        double loanAmt = Double.parseDouble(loanPayment.getAmount());
        double balanceAmt = Double.parseDouble(balance.getBalance());
        if (loanAmt > balanceAmt) {
            String msg = "Not enough money in account to cover necessary amount ".concat(E.PIG)
                    .concat(" \uD83C\uDF4E balance: ".concat(balance.getBalance().concat(" \uD83C\uDF4E loan amount: ")
                            .concat(loanPayment.getAmount())));
            LOGGER.info(msg);
            return false;
        } else {
            LOGGER.info("LoanPayment is less than the account balance. returning TRUE");
            return true;
        }
    }

    private void sendAndSave(LoanApplication application, Client client, String clientSeed, LoanPayment payment)
            throws Exception {

        if (!isWithinBalance(payment)) {
            return;
        }
        PaymentRequest request = new PaymentRequest();
        request.setSeed(clientSeed);
        request.setPaymentRequestId(UUID.randomUUID().toString());
        request.setAmount(payment.getAmount());
        request.setAnchorId(anchor.getAnchorId());
        request.setAssetCode(application.getAssetCode());
        request.setDate(new DateTime().toDateTimeISO().toString());
        //
        request.setDestinationAccount(payment.getAgentAccount());
        stellarPaymentService.sendPayment(request);

        payment.setOnTime(true);
        payment.setPaymentRequestId(request.getPaymentRequestId());

        try {
            payment = agentService.addLoanPayment(payment);
            LOGGER.info(E.PRETZEL.concat(E.PRETZEL)
                    + "Weekly LoanPayment made on Stellar and stored in database; getPaymentRequestId: "
                    .concat(payment.getPaymentRequestId()).concat(E.RED_TRIANGLE));
        } catch (Exception e) {
            // ignore
            if (e.getMessage() == null) {
                LOGGER.info(E.ERROR.concat("Unknown LoanPayment error"));
                e.printStackTrace();
            } else {
                LOGGER.info(E.ERROR.concat(e.getMessage()));
            }

        }

    }

    private void deleteFirebaseArtifacts() throws Exception {
        // delete users and collections
        LOGGER.info(concat
                + "Start deleteFirebaseArtifacts .... deleteAuthUsers  \uD83D\uDECE");
        firebaseService.deleteAuthUsers();
        LOGGER.info(E.SOCCER_BALL.concat(E.SOCCER_BALL) + "Firebase auth users have been cleaned out");
        LOGGER.info(concat
                + "Start deleteFirebaseArtifacts .... deleteCollections  \uD83D\uDECE");
        firebaseService.deleteCollections();
        LOGGER.info(E.BASKET_BALL.concat(E.BASKET_BALL) + "Firestore collections have been cleaned out");
    }

    @Autowired
    private StellarPaymentService stellarPaymentService;

    @Autowired
    private StokvelService stokvelService;
    @Autowired
    private CryptoService cryptoService;

    public void generateLoanApprovals(String anchorId) throws Exception {
        LOGGER.info("\n\n" + E.BLUE_DOT.concat(E.BLUE_DOT)
                .concat("======================= Approval of LoanApplication by Clients and Agents ..."));
        setAnchor();
        int cnt = 0;
        agents = firebaseService.getAgents();
        for (Agent agent : agents) {
            List<LoanApplication> loanApplications = firebaseService.getAgentLoans(agent.getAgentId());
            for (LoanApplication loanApplication : loanApplications) {
                if (!loanApplication.isApprovedByClient()) {
                    agentService.approveApplicationByClient(loanApplication.getLoanId());
                    cnt++;
                }
            }
        }
        LOGGER.info(E.BLUE_DOT.concat(E.BLUE_DOT.concat(E.BLUE_DOT))
                .concat(" .... Clients have completed " + cnt + " loan approvals"));
        int cnt2 = 0;
        for (Agent agent : agents) {
            List<LoanApplication> loanApplications = firebaseService.getAgentLoans(agent.getAgentId());
            for (LoanApplication loanApplication : loanApplications) {
                String seed = cryptoService.getDecryptedSeed(agent.getStellarAccountId());
                loanApplication.setAgentSeed(seed);
                try {
                    if (!loanApplication.isApprovedByAgent()) {
                        agentService.approveApplicationByAgent(loanApplication);
                        cnt2++;
                    }
                } catch (Exception e) {
                    LOGGER.info(E.NOT_OK + "Bad shit, IGNORED ... : ".concat(e.getMessage()));
                }
            }
        }
        LOGGER.info(E.BLUE_DOT.concat(E.BLUE_DOT.concat(E.BLUE_DOT))
                .concat(" .... Agents have completed " + cnt2 + " loan approvals"));
    }

    public void generateAgentFunding() throws Exception {
        LOGGER.info(E.PEAR.concat(E.PEAR.concat(E.PEACH))
                + "...... Generating agent funds ...".concat(E.PEAR.concat(E.PEAR)));
        setAnchor();
        agents = firebaseService.getAgents();
        List<AccountService.AssetBag> assetBags = accountService
                .getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        String seed = cryptoService.getDecryptedSeed(anchor.getDistributionAccount().getAccountId());
        int cnt = 0;
        for (Agent agent : agents) {
            for (AccountService.AssetBag assetBag : assetBags) {
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setSeed(seed);
                paymentRequest.setPaymentRequestId(UUID.randomUUID().toString());
                paymentRequest.setAnchorId(anchor.getAnchorId());
                paymentRequest.setAssetCode(assetBag.getAssetCode());
                paymentRequest.setDate(new DateTime().toDateTimeISO().toString());
                paymentRequest.setDestinationAccount(agent.getStellarAccountId());
                paymentRequest.setAmount(getRandomAgentAmount());
                try {
                    stellarPaymentService.sendPayment(paymentRequest);
                    cnt++;
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("Agent funding failed; ignored for this purpose: ");
                }
            }
        }
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD).concat(
                " Agent Funding complete ...............  \uD83C\uDF51 " + "total funding transactions: " + cnt)));
    }

    public Stokvel generateStokvel(String anchorId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH).concat("Generate Stokvel"));
        Stokvel stokvel = new Stokvel();
        stokvel.setName("OriginalGroup II");
        stokvel.setAnchorId(anchorId);
        stokvel.setPassword(basePassword);
        stokvel.setActive(true);
        stokvel.setDate(new DateTime().toDateTimeISO().toString());
        OrganizationKYCFields fields = new OrganizationKYCFields();
        fields.setEmail("stokvel_".concat("" + System.currentTimeMillis()).concat("@modernachor.com"));
        stokvel.setKycFields(fields);
        stokvel = stokvelService.createStokvel(stokvel);
        LOGGER.info(E.LEMON
                .concat(E.LEMON.concat(E.LEMON).concat("Stokvel generated: ".concat(stokvel.getName()))));
        return stokvel;
    }

    public List<Member> generateStokvelMembers(String stokvelId, String anchorId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH).concat("Generate Member"));
        setFirstNames();
        setLastNames();
        List<Member> list = new ArrayList<>();
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            Member m = new Member();
            m.setStokvelIds(new ArrayList<>());
            m.setAnchorId(anchorId);
            m.getStokvelIds().add(stokvelId);
            m.setActive(true);
            m.setDate(new DateTime().toDateTimeISO().toString());
            m.setExternalAccountId("Are we there yet?");
            m.setPassword(basePassword);
            PersonalKYCFields fields = new PersonalKYCFields();
            int index1 = rand.nextInt(firstNames.size() - 1);
            int index2 = rand.nextInt(lastNames.size() - 1);
            fields.setFirst_name(firstNames.get(index1));
            fields.setLast_name(lastNames.get(index2));
            fields.setEmail_address("member_" + System.currentTimeMillis() + "@modernanchor.com");
            m.setKycFields(fields);
            m = stokvelService.createMember(m);
            list.add(m);
            cnt++;
            LOGGER.info(E.RED_CAR.concat(E.RED_CAR.concat(E.RED_CAR)
                    .concat("Member #" + cnt + " generated: ".concat(m.getFullName()))));
        }
        LOGGER.info(E.RED_CAR
                .concat(E.RED_CAR.concat(E.RED_CAR).concat("Members generated: ".concat("" + list.size()))));
        return list;
    }

    private String getRandomAgentAmount() {
        int num = rand.nextInt(1000);
        if (num < 250)
            return "" + (250 * 1000) + ".00";
        if (num > 249 && num < 500)
            return "" + (500 * 1000) + ".00";
        if (num > 499 && num < 900)
            return "" + (1000 * 1000) + ".00";
        if (num > 899)
            return "" + (2500 * 1000) + ".00";
        int total = num * 100000;
        return "" + total + ".00";
    }

    public void generateLoanApplications() throws Exception {

        setAnchor();
        List<Client> clients = firebaseService.getAnchorClients();
        List<AccountService.AssetBag> assetBags = accountService
                .getDefaultAssets(anchor.getIssuingAccount().getAccountId());
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD)
                .concat(" Generating LoanApplications ............... total clients: " + clients.size())));
        int cnt = 0;
        for (Client client : clients) {
            for (AccountService.AssetBag assetBag : assetBags) {
                LoanApplication app = new LoanApplication();
                app.setAnchorId(client.getAnchorId());
                app.setAgentId(client.getAgentIds().get(0));
                app.setAmount(getRandomAmount());
                app.setAssetCode(assetBag.getAssetCode());
                app.setInterestRate(getRandomInterestRate());
                int num = rand.nextInt(10);
                if (num > 5) {
                    app.setLoanPeriodInMonths(getLoanPeriodInMonths());
                } else {
                    app.setLoanPeriodInWeeks(getLoanPeriodInWeeks());
                }
                app.setClientAccount(client.getStellarAccountId());
                app.setClientId(client.getClientId());
                app.setDate(new DateTime().toDateTimeISO().toString());
                try {
                    LOGGER.info(
                            E.PANDA.concat(E.PANDA).concat(E.PANDA).concat("Generate LoanApplication for: ")
                                    .concat(client.getPersonalKYCFields().getFirst_name()).concat("  \uD83C\uDF51 asset: ")
                                    .concat(assetBag.getAssetCode()).concat(" \uD83D\uDC26 interest rate: "
                                    + app.getInterestRate() + " % ".concat("  \uD83C\uDFB2 ")));
                    agentService.addLoanApplication(app);
                    cnt++;
                } catch (Exception e) {
                    LOGGER.info(("..... \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 \uD83D\uDE21 " + "Interest Rate: "
                            + app.getInterestRate()
                            + " \uD83D\uDE21 LoanApplication failed; ignored for this purpose: ")
                            .concat(e.getMessage()));
                }
            }
        }

        LOGGER.info("\n\n" + E.YELLOW_BIRD.concat(E.YELLOW_BIRD.concat(E.YELLOW_BIRD)
                .concat(" LoanApplications generation completed ............... total loans: " + cnt)));
    }

    private int getLoanPeriodInMonths() {
//        int num = rand.nextInt(6);
//        if (num == 0)
//            return 3;
        return 3;
    }

    private int getLoanPeriodInWeeks() {
//        int num = rand.nextInt(4);
//        if (num == 0)
//            return 2;
        return 2;
    }

    private String getRandomAmount() {
        int num = rand.nextInt(100);
        if (num < 3)
            num = 10;
        int total = num * 100;
        return "" + total + ".00";
    }

    private double getRandomInterestRate() {
        int num = rand.nextInt(20);
        if (num < 5)
            num = 8;
        return num * 1.5;
    }

    @Autowired
    private TOMLService tomlService;

    private Anchor addAnchor(String anchorName) throws Exception {
        // create anchor bag ...
        AnchorBag bag = new AnchorBag();
        bag.setFundingSeed(seed);
        bag.setAssetAmount("100000000000");
        bag.setStartingBalance("9900");
        bag.setDistributionBalance("100000000000.00");
        bag.setPassword(basePassword);

        Anchor mAnchor = new Anchor();
        mAnchor.setName(anchorName);
        mAnchor.setEmail("anchor_".concat("" + new DateTime().getMillis()).concat("@anchor.com"));
        mAnchor.setCellphone("+27911447786");
        mAnchor.setDate(new DateTime().toDateTimeISO().toString());

        bag.setAnchor(mAnchor);
        AnchorController controller = context.getBean(AnchorController.class);
        Anchor m = controller.createAnchor(bag);
        LOGGER.info("\uD83D\uDC9A \uD83D\uDC9A \uD83D\uDC9A \uD83D\uDC9A Anchor created: "
        + m.getName());

        return m;

    }

    private NetworkOperatorDTO buildNetworkOperator(String anchorName) throws Exception {

        List<TradeMatrixItemDTO> matrixItemDTOS = new ArrayList<>();

        TradeMatrixItemDTO m1 = new TradeMatrixItemDTO(10000.00,
                100000.00, 15.5, new DateTime().toDateTimeISO().toString());

        TradeMatrixItemDTO m2 = new TradeMatrixItemDTO(100001.00,
                500000.00, 10.0, new DateTime().toDateTimeISO().toString());

        TradeMatrixItemDTO m3 = new TradeMatrixItemDTO(500001.00,
                1000000.00, 6.5, new DateTime().toDateTimeISO().toString());

        TradeMatrixItemDTO m4 = new TradeMatrixItemDTO(1000001.00,
                25000000.00, 4.5, new DateTime().toDateTimeISO().toString());


        matrixItemDTOS.add(m1);
        matrixItemDTOS.add(m2);
        matrixItemDTOS.add(m3);
        matrixItemDTOS.add(m4);

        NetworkOperatorDTO operator = new NetworkOperatorDTO();

        operator.setIssuedBy("SELF");
        operator.setAccountId(UUID.randomUUID().toString());
        operator.setMinimumInvoiceAmount(10000.00);
        operator.setMaximumInvoiceAmount(1000000.00);
        operator.setMaximumInvestment(250000000.00);
        operator.setTradeFrequencyInMinutes(30);
        operator.setName(anchorName + " (Network Operator)");
        operator.setEmail("netoperator@bfn.com");
        operator.setCellphone("099 999 9999");
        operator.setPassword("pass123");
        operator.setDefaultOfferDiscount(10);
        operator.setTradeMatrixItems(matrixItemDTOS);
        operator.setUid(UUID.randomUUID().toString());

        return operator;

    }

    private List<Agent> agents = new ArrayList<>();

    private void addAgents(Anchor anchor) throws Exception {
        LOGGER.info(E.ALIEN.concat(E.ALIEN.concat(E.LEAF))
                + " - \uD83D\uDC26 Creating agents .....");
        Agent agent1 = buildAgent(anchor);
        agent1.getPersonalKYCFields().setFirst_name("Tiger");
        agent1.getPersonalKYCFields().setLast_name("Wannamaker");
        agent1.setAnchorId(anchor.getAnchorId());
        agents.add(agentService.createAgent(agent1));
        LOGGER.info(E.ALIEN.concat(E.ALIEN.concat(E.LEAF))
                .concat("Agent Wannamaker created OK: ".concat(G.toJson(agent1))));

        Agent agent3 = buildAgent(anchor);
        agent3.getPersonalKYCFields().setFirst_name("Kgabzela");
        agent3.getPersonalKYCFields().setLast_name("Marule-Smythe");
        agent3.setAnchorId(anchor.getAnchorId());
        agents.add(agentService.createAgent(agent3));
        LOGGER.info(E.ALIEN.concat(E.ALIEN.concat(E.LEAF)).concat(agent3.getFullName()
                + " - \uD83D\uDC26 Agent Marule-Smythe created OK: ".concat(agent3.getFullName())));
    }

    public void generateAgentClients(int count, Anchor anchor) throws Exception {
        LOGGER.info(E.ALIEN.concat(E.ALIEN.concat(E.LEAF))
                + " - \uD83D\uDC26 Creating clients for the agents .....");
        setFirstNames();
        setLastNames();
        Toml toml = tomlService.getAnchorToml();
        if (toml == null) {
            throw new Exception("Generator: Anchor TOML file is missing: ");
        }
        agents = firebaseService.getAgents();
        for (Agent agent : agents) {
            for (int i = 0; i < count; i++) {
                Client c1 = buildClient(agent.getAgentId(), anchor);
                c1.setAnchorId(anchor.getAnchorId());
                Client result = agentService.createClient(c1);
                LOGGER.info(E.LEMON.concat(E.LEMON).concat(".......... agent Client created: ")
                        .concat(result.getPersonalKYCFields().getFirst_name()));
            }
        }
    }

    private Client buildClient(String agentId, Anchor mAnchor) {
        Client c = new Client();
        c.setAnchorId(mAnchor.getAnchorId());
        c.setAgentIds(new ArrayList<>());
        c.getAgentIds().add(agentId);
        c.setDateRegistered(new DateTime().toDateTimeISO().toString());
        c.setDateUpdated(new DateTime().toDateTimeISO().toString());
        c.setPassword("pass123");
        c.setStartingFiatBalance("0.01");
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("client" + System.currentTimeMillis() + "@anchor.com");
        fields.setFirst_name(firstNames.get(rand.nextInt(firstNames.size() - 1)));
        fields.setLast_name(lastNames.get(rand.nextInt(lastNames.size() - 1)));
        c.setPersonalKYCFields(fields);
        return c;
    }

    @Value("${fiatLimit}")
    private String fiatLimit;

    private Agent buildAgent(Anchor mAnchor) {
        Agent agent1 = new Agent();
        agent1.setAnchorId(mAnchor.getAnchorId());
        agent1.setDateRegistered(new DateTime().toDateTimeISO().toString());
        agent1.setDateUpdated(new DateTime().toDateTimeISO().toString());
        agent1.setFiatBalance("0.01");
        agent1.setFiatLimit(fiatLimit);
        agent1.setPassword("pass123");
        PersonalKYCFields fields = new PersonalKYCFields();
        fields.setMobile_number("+27998001212");
        fields.setEmail_address("a" + System.currentTimeMillis() + "@anchor.com");
        agent1.setPersonalKYCFields(fields);
        return agent1;
    }

    List<String> firstNames = new ArrayList<>();
    List<String> lastNames = new ArrayList<>();
    Random rand = new Random(System.currentTimeMillis());

    private void setFirstNames() {
        firstNames.add("Anna");
        firstNames.add("Charlie");
        firstNames.add("Thabo");
        firstNames.add("Vusi");
        firstNames.add("Mmathabo");
        firstNames.add("Nana");
        firstNames.add("Marks");
        firstNames.add("Charlie");
        firstNames.add("Ouma");
        firstNames.add("Darren");
        firstNames.add("Sammy");
        firstNames.add("Cookie");
        firstNames.add("Brandy");
        firstNames.add("Riley");
        firstNames.add("Maria");
        firstNames.add("Mmabatho");
        firstNames.add("Mmapule");
        firstNames.add("Johnny");
        firstNames.add("Xavier");
        firstNames.add("Freddie");
        firstNames.add("Butiki");
        firstNames.add("Samuel");
        firstNames.add("David");
        firstNames.add("Peter");
        firstNames.add("Mashamba");
        firstNames.add("John");
        firstNames.add("Bobby");
        firstNames.add("Gilbert");
        firstNames.add("Sydney");
        firstNames.add("Don Ray");
        firstNames.add("Tsakane");
        firstNames.add("Fiona");
        firstNames.add("Kgabi");
        firstNames.add("Morena");
        firstNames.add("Ouma");
        firstNames.add("Lucy");
    }

    private void setLastNames() {
        lastNames.add("Maluleke");
        lastNames.add("Mhinga");
        lastNames.add("Macheke");
        lastNames.add("Masinga");
        lastNames.add("Moleketi");
        lastNames.add("Morakane");
        lastNames.add("Mokone");
        lastNames.add("Raymond");
        lastNames.add("Donald");
        lastNames.add("Mshengu");
        lastNames.add("Manthata");
        lastNames.add("Sithole");
        lastNames.add("Mafumbedzi");
        lastNames.add("Sono");
        lastNames.add("Macheke");
        lastNames.add("Mokone");
        lastNames.add("Mokoena");
        lastNames.add("Ntuli");
        lastNames.add("Nkuna");
        lastNames.add("Nkosi");
        lastNames.add("Maringa");
        lastNames.add("Titi");
        lastNames.add("Johnson");

        lastNames.add("van der Merwe");
        lastNames.add("Smith");
        lastNames.add("Thunberg");
        lastNames.add("Zuckerberg");
        lastNames.add("Lennon");
        lastNames.add("Mashamba");
        lastNames.add("Johnson");

        lastNames.add("Bodibe");
        lastNames.add("Rhangane");
        lastNames.add("Buthelezi");
    }
}
