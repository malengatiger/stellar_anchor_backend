package com.anchor.api;

import com.anchor.api.controllers.AnchorController;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@SpringBootApplication
@RefreshScope
@EnableScheduling
public class AnchorBackendApplication implements ApplicationListener<ApplicationReadyEvent> {
    public static final Logger LOGGER = LoggerFactory.getLogger(AnchorBackendApplication.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat(Emoji.PANDA) +
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
        LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat(Emoji.PANDA) +
                " AnchorApplication started OK! ".concat(Emoji.HAND2.concat(Emoji.HAND2))
                + " All services up and running.");
    }

    private static String getBanner() {
        StringBuilder sb = new StringBuilder();
        sb.append("###############################################\n");
        sb.append("#### " + Emoji.HEART_BLUE + "ANCHOR BANK NETWORK SERVICES " + Emoji.HEART_BLUE + "   ####\n");
        sb.append("#### ".concat(Emoji.FLOWER_RED).concat(" ").concat(new DateTime().toDateTimeISO().toString().concat("     ####\n")));
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
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C AnchorBackendApplication: onApplicationEvent: " +
                "ApplicationReadyEvent fired: \uD83C\uDF3C \uD83C\uDF3C app is ready to initialize Firebase .... ");
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: DEVELOPMENT STATUS: " +
                "\uD83C\uDF51 " + status + " \uD83C\uDF51 ");
        LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: ACTIVE PROFILE : " +
                "\uD83C\uDF51 " + activeProfile + " \uD83C\uDF51 ");
        accountService.printStellarHorizonServer();

        Calendar cal = Calendar.getInstance();
        int res = cal.getActualMaximum(Calendar.DATE);
        LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL) + "Today's Date = " + cal.getTime());

        LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL) +
                "Last Date of the current month = " + res + Emoji.SKULL.concat(Emoji.SKULL));

        try {
            //
            accountService.listenForTransactions();
            firebaseService.initializeFirebase();
            for (Method method : accountService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(Emoji.FLOWER_YELLOW.concat(Emoji.FLOWER_YELLOW).concat("AccountService method: "
                            .concat(method.getName()).concat(" - " + Emoji.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : firebaseService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(Emoji.FLOWER_PINK.concat(Emoji.FLOWER_PINK).concat("FirebaseService method: "
                            .concat(method.getName()).concat(" - " + Emoji.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : agentService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(Emoji.HAND3.concat(Emoji.HAND3).concat("AgentService method: "
                            .concat(method.getName()).concat(" - " + Emoji.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            for (Method method : anchorAccountService.getClass().getMethods()) {
                if (isValid(method.getName()))
                    LOGGER.info(Emoji.FLOWER_RED.concat(Emoji.FLOWER_RED).concat("AnchorAccountService method: "
                            .concat(method.getName()).concat(" - " + Emoji.RED_APPLE)));
            }
            LOGGER.info("\n\n");
            AnchorController controller = context.getBean(AnchorController.class);
            String tom = controller.getWellKnownStellarToml();
            LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL)
                    + Emoji.SKULL.concat(Emoji.SKULL)
                    + "  ------------ STELLAR TOML FILE ------------\n"
                    + tom + " " + Emoji.SKULL.concat(Emoji.SKULL) + "\n\n");

        } catch (Exception e) {
            LOGGER.info(" \uD83C\uDF45 Firebase initialization FAILED");
            e.printStackTrace();
        }

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
