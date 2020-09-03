package com.anchor.api.controllers.stellar;

import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.stellar.AgentService;
import com.anchor.api.services.stellar.AnchorAccountService;
import com.anchor.api.services.misc.TOMLService;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
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
    private TOMLService tomlService;
    @Value("${status}")
    private String status;


    @GetMapping(value = "/generateAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor generateAnchor(String anchorName) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAnchor ...");
        Anchor anchor = demoDataGenerator.createNewAnchor(anchorName);
        LOGGER.info(E.DICE.concat(E.DICE.concat(E.DICE)
                .concat("New Anchor Returned")
                .concat(G.toJson(anchor))));
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData:generateAnchor completed and returning external caller... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status);

        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C PREPARE STELLAR.TOML and ANCHOR.TOML - edit ANCHOR ID, ISSUING ACCOUNTS etc.  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C UPLOAD STELLAR.TOML and ANCHOR.TOML to cloud storage               \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C Retrieve account info from Firestore or logs                       \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");
        LOGGER.info(G.toJson(anchor));

        return anchor;
    }
    @GetMapping(value = "/generateDemo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateDemo() throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateDemo ...");
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

    @GetMapping(value = "/generateAgentClients", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentClients( @RequestParam int count) throws Exception {
        checkTOML();
        demoDataGenerator.generateAgentClients(count);
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
        demoDataGenerator.generateAgentClients( count);
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
