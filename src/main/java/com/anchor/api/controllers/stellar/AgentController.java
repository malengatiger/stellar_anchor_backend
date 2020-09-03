package com.anchor.api.controllers.stellar;

import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.*;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.services.payments.PaymentService;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.stellar.AgentService;
import com.anchor.api.services.stellar.AnchorAccountService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
public class AgentController {
    public static final Logger LOGGER = LoggerFactory.getLogger(AgentController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public AgentController() {
        LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C AgentController  " +
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
    @Value("${status}")
    private String status;

    /**
     * This endpoint creates an Agent. It is called by the Anchor itself or is part of a registration by the Agent
     * on some kind of app (web or mobile). The Agent, in turn,
     *
     * @param agent agent
     * @return A new Agent
     * @throws Exception exception thrown
     */
    @PostMapping(value = "/createAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Agent createAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:createAgent ...");

        //todo - externalize variables .....
        Agent mAgent = agentService.createAgent(agent);
        LOGGER.info(E.LEAF.concat(E.LEAF) + "Anchor returns Agent with a new Stellar account:" +
                " \uD83C\uDF4E "
                + G.toJson(mAgent));
        return mAgent;

    }

    @PostMapping(value = "/updateAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateAgent(@RequestBody Agent agent) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:createAgent ...");
        String message = agentService.updateAgent(agent);
        LOGGER.info(E.LEAF.concat(E.LEAF) + message);
        return message;
    }

    @PostMapping(value = "/approveApplicationByAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveApplicationByAgent(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:approve ...");
        LoanApplication application = agentService.approveApplicationByAgent(loanApplication);
        LOGGER.info(E.LEAF.concat(E.LEAF) + application.getAmount() + " "
                + application.getAssetCode());
        return application;
    }

    //approveApplicationByClient
    @PostMapping(value = "/approveApplicationByClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public String approveApplicationByClient(
            @RequestParam String loanId) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS)
                + "AgentController:approveApplicationByClient ...");
        String msg = agentService.approveApplicationByClient(loanId);
        LOGGER.info(E.LEAF.concat(E.LEAF) + msg);
        return msg;
    }

    @PostMapping(value = "/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication declineApplication(@RequestBody LoanApplication agent) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:decline ...");
        LoanApplication application = agentService.declineApplication(agent);
        LOGGER.info(E.LEAF.concat(E.LEAF) + application.getAmount() + " "
                + application.getAssetCode());
        return application;
    }

    @PostMapping(value = "/makeLoanPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanPayment makeLoanPayment(@RequestBody LoanPayment loanPayment) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:makeLoanPayment ...");
        LoanPayment payment = agentService.addLoanPayment(loanPayment);
        LOGGER.info(E.LEAF.concat(E.LEAF) + loanPayment.getAmount() + " "
                + loanPayment.getAssetCode());
        return payment;
    }

    @PostMapping(value = "/addOrganization", produces = MediaType.APPLICATION_JSON_VALUE)
    public Organization addOrganization(@RequestBody Organization organization) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:addOrganization ...");

        Organization org = agentService.addOrganization(organization);
        LOGGER.info(E.LEAF.concat(E.LEAF) + G.toJson(org));
        return org;
    }

    @PostMapping(value = "/loanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication loanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS).concat(E.RAIN_DROPS)
                + "AgentController:loanApplication ...");
        LoanApplication org = agentService.addLoanApplication(loanApplication);
        LOGGER.info(E.LEAF.concat(E.LEAF) + G.toJson(org));
        return org;
    }

    @PostMapping(value = "/approveLoanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication approveLoanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:approveLoanApplication ...");
        LoanApplication org = agentService.approveApplicationByAgent(loanApplication);
        LOGGER.info(E.LEAF.concat(E.LEAF) + G.toJson(org));
        return org;
    }

    @Autowired
    private PaymentService paymentService;

    // @PostMapping(value = "/sendPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    // public boolean sendPayment(@RequestBody PaymentRequest paymentRequest) throws Exception {
    //     PaymentRequest response = paymentService.sendPayment(paymentRequest);
    //     LOGGER.info(Emoji.LEAF.concat(Emoji.LEAF).concat("Payment sent? ")
    //             .concat("" + response.isSuccess()));

    //     return true;
    // }

    @PostMapping(value = "/declineLoanApplication", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanApplication declineLoanApplication(@RequestBody LoanApplication loanApplication) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:declineLoanApplication ...");
        LoanApplication org = agentService.declineApplication(loanApplication);
        LOGGER.info(E.LEAF.concat(E.LEAF) + G.toJson(org));
        return org;
    }

    @PostMapping(value = "/loanPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanPayment loanPayment(@RequestBody LoanPayment loanPayment) throws Exception {
        LOGGER.info(E.RAIN_DROPS.concat(E.RAIN_DROPS) + "AgentController:loanPayment ...");
        LoanPayment org = agentService.addLoanPayment(loanPayment);
        LOGGER.info(E.LEAF.concat(E.LEAF) + G.toJson(org));
        return org;
    }


    @GetMapping(value = "/retrieveKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public String retrieveKey(@RequestParam String agentId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:retrieveKey ...");
        String key = accountService.retrieveKey(agentId);
        LOGGER.info(E.LEAF.concat(E.LEAF) + " key found:" + key);
        return key;
    }

    @GetMapping(value = "/getLoanPayments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanPayment> getLoanPayments(@RequestParam String loanId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:getLoanPayments ...");
        List<LoanPayment> loanPayments = agentService.getLoanPayments(loanId);
        LOGGER.info(E.LEAF.concat(E.LEAF) + " payments found:" + loanPayments.size());
        return loanPayments;
    }

    @Autowired
    private FirebaseService firebaseService;

    @GetMapping(value = "/getAgents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agent> getAgents() throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:getAgents ...");

        List<Agent> agents = firebaseService.getAgents();
        LOGGER.info(E.LEAF.concat(E.LEAF).concat(
                ("Found " + agents.size() + " agents for this Anchor ")));
        return agents;
    }

    @GetMapping(value = "/getPaymentRequests", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PaymentRequest> getPaymentRequests() throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:getPaymentRequests ...");

        List<PaymentRequest> requests = firebaseService.getPaymentRequests();
        LOGGER.info(E.LEAF.concat(E.LEAF).concat(
                ("Found " + requests.size() + " PaymentRequests for this Anchor "))
        );
        return requests;
    }

    @GetMapping(value = "/getAgentClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> getAgentClients(@RequestParam String agentId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:getAgentClients ...");
        List<Client> agentClients = agentService.getAgentClients(agentId);
        LOGGER.info(E.LEAF.concat(E.LEAF).concat(
                ("Found " + agentClients.size() + " clients for this Agent ")));
        return agentClients;
    }

    @GetMapping(value = "/getAgentLoans", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanApplication> getAgentLoans(@RequestParam String agentId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:getAgentLoans ...");
        List<LoanApplication> agentLoans = agentService.getAgentLoans(agentId);
        LOGGER.info(E.LEAF.concat(E.LEAF).concat
                (" found " + agentLoans.size() + " "));
        return agentLoans;
    }

    @GetMapping(value = "/removeClient", produces = MediaType.TEXT_PLAIN_VALUE)
    public String removeClient(@RequestParam String clientId) throws Exception {
        LOGGER.info(E.PEACH.concat(E.PEACH) + "AgentController:removeClient ...");
        String message = agentService.removeClient(clientId);
        LOGGER.info(E.LEAF.concat(E.LEAF) + message);
        return message;
    }





}
