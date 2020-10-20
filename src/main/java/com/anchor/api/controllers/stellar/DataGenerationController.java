package com.anchor.api.controllers.stellar;

import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.models.NetworkOperatorDTO;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.stellar.AgentService;
import com.anchor.api.services.stellar.AnchorAccountService;
import com.anchor.api.services.misc.TOMLService;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.E;
import com.anchor.api.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RequestMapping("/data")
@RestController

public class DataGenerationController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
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
    @Autowired
    private NetworkUtil networkUtil;
    @Autowired
    private TOMLService tomlService;
    @Value("${status}")
    private String status;
    @Value("${bfnUrl}")
    private String bfnUrl;


    @GetMapping(value = "/generateAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor generateAnchor(@RequestParam String anchorName, @RequestParam String fundingSeed,
                                 @RequestHeader("Authorization") String token) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 " +
                "DataGenerationController:generateAnchor ... \uD83D\uDD35: "+ anchorName);
        Anchor anchor;

        if (token != null) {
            String mToken = token.substring(7);
            LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C generateAnchor: auth mToken:" + mToken + " \uD83D\uDC9C");
            anchor = demoDataGenerator.createNewAnchor(anchorName, fundingSeed, mToken);
        } else {
            anchor = demoDataGenerator.createNewAnchor(anchorName, fundingSeed, null);
        }

        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData:generateAnchor completed and returning anchor "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status);
        LOGGER.info(E.DICE.concat(E.DICE.concat(E.DICE)
                .concat("New Anchor Returned: ")
                .concat(G.toJson(anchor))));

        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 0. Create Stellar Anchor: localhost:8084/anchor/data/generateAnchor  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 1. CHECK ANCHOR and NETWORK OPERATOR on Firestore  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 2. PREPARE STELLAR.TOML and ANCHOR.TOML - edit ANCHOR ID, ISSUING ACCOUNTS etc.  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 3. UPLOAD STELLAR.TOML and ANCHOR.TOML to cloud storage               \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 4. Generate Stellar Anchor Demo Data: localhost:8084/anchor/data/generateDemo  \uD83D\uDC99 \uD83D\uDC9C");


        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 5. Start clean BFN Network: ./scripts/justnodes.sh   \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 6. Create Network Operator with Accounts:: localhost:10050/bfn/demo/generateAnchorNodeData?numberOfAccounts=4  \uD83D\uDC99 \uD83D\uDC9C");

        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 7. Generate Customers: localhost:10053/bfn/demo/generateCustomerNodeData  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C 8. Generate BFN Anchor Node Demo Data: localhost:10050/bfn/demo/generateAnchorNodeAccounts?numberOfAccounts=4  \uD83D\uDC99 \uD83D\uDC9C");

        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");

        return anchor;
    }


    @PostMapping(value = "/generateNetworkOperator", produces = MediaType.APPLICATION_JSON_VALUE)
    public NetworkOperatorDTO createBFNNetworkOperator(NetworkOperatorDTO operator) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /createBFNNetworkOperator ... \uD83D\uDD35: " + bfnUrl);
        if (operator.getTradeMatrixItems() == null || operator.getTradeMatrixItems().isEmpty()) {
            throw new Exception("Missing tradeMatrixItems");
        }
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC9C Network Operator to be created: ".concat(G.toJson(operator)));
        NetworkOperatorDTO m = networkUtil.createBFNNetworkOperator(bfnUrl + "createNetworkOperator", operator);
        LOGGER.info("\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData:generateNetworkOperator completed and returning anchor "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status);
        LOGGER.info(E.DICE.concat(E.DICE.concat(E.DICE)
                .concat("New NetworkOperator Returned .... ")
                .concat(G.toJson(m))));
        return m;
    }

    @GetMapping(value = "/generateDemo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateDemo() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication data/generateDemo ...");
        checkTOML();
        demoDataGenerator.startGeneration();
        return "\n\n\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }


    private void checkTOML() throws Exception {
        Toml toml = tomlService.getAnchorToml();
        if (toml == null) {
            LOGGER.severe(E.ERROR+ E.ERROR+ E.ERROR+ " Missing anchor.toml, QUITTING! - " +
                    "Please upload an anchor.toml first before trying this again. " + E.ERROR+ E.ERROR);
            throw new Exception("anchor.toml file missing");
        }
    }

    @Autowired
    private FirebaseService firebaseService;
    @GetMapping(value = "/generateAgentClients", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentClients( @RequestParam int count) throws Exception {
        checkTOML();
        Anchor anchor = firebaseService.getAnchor();
        demoDataGenerator.generateAgentClients(count, anchor);
        return "\uD83D\uDC99 \uD83D\uDC9C generateAgentClients completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoans", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoans() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoans ...");
        demoDataGenerator.generateLoanApplications();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoans completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateStokvel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Stokvel generateStokvel(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvel ...");
        Stokvel stokvel = demoDataGenerator.generateStokvel(anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvel completed ... "
                + stokvel.getName() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateClients( @RequestParam int count) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateClients ..." +
                 "  count: " + count);
        checkTOML();
        Anchor anchor = firebaseService.getAnchor();
        demoDataGenerator.generateAgentClients( count, anchor);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C generateAgentClients completed ... \uD83C\uDF3C \uD83C\uDF3C "
               + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return msg;
    }
    @GetMapping(value = "/generateStokvelMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> generateStokvelMembers(@RequestParam String anchorId, @RequestParam String stokvelId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvelMembers ...");
        List<Member> stokvel = demoDataGenerator.generateStokvelMembers(stokvelId, anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvelMembers completed ... "
                + stokvel + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateAgentFunding", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentFunding() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAgentFunding ...");
        demoDataGenerator.generateAgentFunding();
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateAgentFunding completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoanApprovals", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoanApprovals(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoanApprovals ...");
        demoDataGenerator.generateLoanApprovals(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoanApprovals completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
    @GetMapping(value = "/generatePayments", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generatePayments() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generatePayments ...");
        demoDataGenerator.generatePayments();
        return "\uD83D\uDC99 \uD83D\uDC9C GeneratePayments completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
}
