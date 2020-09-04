package com.anchor.api;

import com.anchor.api.controllers.payments.OzowController;
import com.anchor.api.controllers.payments.PayPalController;
import com.anchor.api.controllers.payments.PayfastController;
import com.anchor.api.services.misc.SchedulerToo;
import com.anchor.api.services.payments.InterLedgerService;
import com.anchor.api.services.payments.StellarPaymentService;
import com.anchor.api.services.pubsub.PublisherService;
import com.anchor.api.services.pubsub.SubscriberService;
import com.anchor.api.services.payments.RipplePaymentService;
import com.anchor.api.controllers.stellar.AnchorController;
import com.anchor.api.services.stellar.AccountService;
import com.anchor.api.services.stellar.AgentService;
import com.anchor.api.services.stellar.AnchorAccountService;
import com.anchor.api.services.misc.FirebaseService;
import com.anchor.api.util.E;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.xpring.ilp.model.AccountBalance;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@SpringBootApplication
@RefreshScope
@EnableScheduling
public class AnchorBackendApplication implements ApplicationListener<ApplicationReadyEvent> {
    public static final Logger LOGGER = LoggerFactory.getLogger(AnchorBackendApplication.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        LOGGER.info(E.PANDA.concat(E.PANDA).concat(E.PANDA) +
                " AnchorApplication starting ...");
        SpringApplication app = new SpringApplication(AnchorBackendApplication.class);
        app.setLogStartupInfo(true);
        app.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment,
                                    Class<?> sourceClass,
                                    PrintStream out) {
                //LOGGER.info(G.toJson(environment.getActiveProfiles()));
                out.println(getBanner());
            }
        });

        app.run(args);
        LOGGER.info(E.PANDA.concat(E.PANDA).concat(E.PANDA) +
                " AnchorApplication started OK! ".concat(E.HAND2.concat(E.HAND2))
                + " All services up and running.");
    }

    private static String getBanner() {
        StringBuilder sb = new StringBuilder();
        sb.append("###############################################\n");
        sb.append("#### " + E.HEART_BLUE + "ANCHOR BANK NETWORK SERVICES " + E.HEART_BLUE + "   ####\n");
        sb.append("#### ".concat(E.FLOWER_RED).concat(" ").concat(new DateTime().toDateTimeISO().toString().concat("     ####\n")));
        sb.append("###############################################\n");
        return sb.toString();
    }

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AnchorAccountService anchorAccountService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ApplicationContext context;

    @Value("${status}")
    private String status;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private SchedulerToo schedulerToo;

    @Autowired
    private OzowController ozowController;

    @Autowired
    private PayfastController payfastController;

    @Autowired
    private PayPalController payPalController;


    @Autowired
    private PublisherService publisherService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private RipplePaymentService ripplePaymentService;

    @Autowired
    private StellarPaymentService stellarPaymentService;

    @Autowired
    private InterLedgerService ilpService;


    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C \uD83C\uDF51 STELLAR :: \uD83C\uDF51 AnchorBackendApplication: onApplicationEvent: " +
                "ApplicationReadyEvent fired: \uD83C\uDF3C \uD83C\uDF3C app is ready to initialize Firebase .... ");
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: DEVELOPMENT STATUS: " +
                "\uD83C\uDF51 " + status + " \uD83C\uDF51 ");
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: ACTIVE PROFILE : " +
                "\uD83C\uDF51 " + activeProfile + " \uD83C\uDF51 ");
        accountService.printStellarHorizonServer();

        Calendar cal = Calendar.getInstance();
        int res = cal.getActualMaximum(Calendar.DATE);
        LOGGER.info(E.SKULL.concat(E.SKULL) + "Today's Date = " + cal.getTime());

        LOGGER.info(E.SKULL.concat(E.SKULL) +
                "Last Date of the current month = " + res + E.SKULL.concat(E.SKULL));

        try {
            //
            accountService.listenForTransactions();
            firebaseService.initializeFirebase();
            for (Method method : accountService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW).concat("AccountService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : firebaseService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_PINK.concat(E.FLOWER_PINK).concat("FirebaseService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : agentService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.HAND3.concat(E.HAND3).concat("AgentService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : anchorAccountService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_RED.concat(E.FLOWER_RED).concat("AnchorAccountService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            AnchorController controller = context.getBean(AnchorController.class);
            Map<String, Object> tom = controller.getWellKnownStellarToml();
            LOGGER.info(E.SKULL.concat(E.SKULL)
                    + E.SKULL.concat(E.SKULL)
                    + "  ------------ STELLAR TOML FILE ------------\n"
                    + tom + " " + E.SKULL.concat(E.SKULL) + "\n\n");

        } catch (Exception e) {
            LOGGER.info(" \uD83C\uDF45 Firebase initialization FAILED");
            e.printStackTrace();
        }
        ///
        try {
            LOGGER.info("\uD83C\uDF38 \uD83C\uDF38 \uD83C\uDF38 onApplicationEvent: calling Publisher Service ");
            String msg = E.PEAR.concat(E.PEAR).concat("Network Payment Services started and ready to serve ".concat(E.CAT)
                    .concat(new Date().toString().concat(" ".concat(E.BLUE_BIRD.concat(E.BLUE_BIRD)))));

            for (Method method : stellarPaymentService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW).concat("StellarPaymentService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            for (Method method : ripplePaymentService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_YELLOW.concat(E.FLOWER_YELLOW).concat("RipplePaymentService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : publisherService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(E.FLOWER_PINK.concat(E.FLOWER_PINK).concat("PublisherService method: "
                            .concat(method.getName()).concat(" - " + E.RED_APPLE)));
            }


            AccountBalance balance = ilpService.testILP();
            LOGGER.info("\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C\uD83E\uDD6C AccountBalance from ILP: ".concat(G.toJson(balance)));
            publisherService.publish(msg, "ozow-success");
        } catch (Exception e) {
            LOGGER.info("\uD83D\uDC7F \uD83D\uDC4E\uD83C\uDFFD  \uD83D\uDC4E\uD83C\uDFFD \uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D PUBLISH FAILED: " + e.getMessage()
                    + " \uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D\uD83D\uDC7D  \uD83D\uDC4E\uD83C\uDFFD \uD83D\uDC7F");
            //e.printStackTrace();
        }

        LOGGER.info("\uD83C\uDFB2 \uD83C\uDFB2 \uD83C\uDFB2 Network Payment Services Configuration Server URIs");

        ozowController.printOzowCallbacks();
        payfastController.printPayfastCallbacks();
        payPalController.printPayPalCallbacks();

    }

    private boolean isValid(String methodName) {
        switch (methodName) {
            case "wait":
            case "equals":
            case "toString":
            case "hashCode":
            case "getClass":
            case "notify":
            case "notifyAll":
                return false;
        }

        return true;
    }
}
//11140
