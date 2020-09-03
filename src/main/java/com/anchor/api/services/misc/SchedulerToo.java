package com.anchor.api.services.misc;


import com.anchor.api.controllers.stellar.ClientController;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.util.E;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

@Component
public class SchedulerToo {
    public static final Logger LOGGER = Logger.getLogger(SchedulerToo.class.getSimpleName());
    @Autowired
    private ApplicationContext context;
    @Value("${anchorName}")
    private String anchorName;
    @Autowired
    private TOMLService tomlService;

    public SchedulerToo() {
        LOGGER.info(E.YELLOW_BIRD.concat(E.YELLOW_BIRD) +
                "Scheduler constructed. Waiting to be triggered ".concat(E.YELLOW_BIRD));
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
//    @Scheduled(fixedRate = 1000 * 15)
    public void fixedRateScheduled() throws Exception {
        LOGGER.info(E.PRETZEL.concat(E.PRETZEL).concat(E.PRETZEL) + "Fixed Rate scheduler; " +
                "\uD83C\uDF3C CALCULATE LOAN BALANCES or OTHER NECESSARY WORK: " + new DateTime().toDateTimeISO().toString()
                + " " + E.RED_APPLE);

        try {

            FirebaseService firebaseService = context.getBean(FirebaseService.class);
            List<Anchor> anchors = firebaseService.getAnchors();
            if (!anchors.isEmpty()) {
                Anchor anchor = anchors.get(0);

                List<Agent> list = firebaseService.getAgents();
                for (Agent agent : list) {
                    LOGGER.info(E.DICE.concat(E.DICE) + "Agent: ".concat(agent.getFullName()).concat(" ")
                            .concat(E.HEART_BLUE));
                }
                Toml toml = tomlService.getAnchorToml();
                if (toml != null) {
                    LOGGER.info(E.DICE.concat(E.DICE) + "  ANCHOR TOML FILE " + E.RED_APPLE + E.RED_APPLE);
                    LOGGER.info("\n" + E.DICE.concat(E.DICE) + toml.toMap().toString() + E.DICE.concat(E.DICE));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(E.NOT_OK.concat(E.NOT_OK) + "Firebase query or anchor toml retrieval fell down");
        }
    }

    public void fileCleanUp() {

        File directory = new File("/");
        File test = new File(directory,"newFile.txt");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C test file: "+  test.getAbsolutePath() +
                "  \uD83D\uDC99 \uD83D\uDC9C");
        if (directory.exists()) {
            LOGGER.info(E.HASH.concat(E.HASH).concat(E.HASH)
                    + "Directory ".concat(directory.getAbsolutePath()));
            File[] list = directory.listFiles();
            if ( list != null){
                for (File file : list) {
                    LOGGER.info(E.HASH.concat(E.HASH) + "File ".concat(file.getAbsolutePath()));
                }
            }
        } else {
            LOGGER.info(E.HASH.concat(E.HASH).concat(E.PEPPER).concat(E.PEPPER) + "Directory "
                    .concat(ClientController.DIRECTORY).concat(" does not exist. CleanUp not necessary"));
        }

    }

//    private void removeDirectory(File directory) {
//        LOGGER.info(Emoji.PEAR.concat(Emoji.PEAR.concat("Directory to be cleaned out: ").concat(directory.getAbsolutePath())));
//        if (directory.isDirectory()) {
//            File[] files = directory.listFiles();
//            if (files != null) {
//                LOGGER.info(Emoji.RAIN_DROP.concat(Emoji.RAIN_DROP) + "Files to be cleaned out : " + files.length);
//                for (File file : files) {
//                    boolean deleted = file.delete();
//                    LOGGER.info(Emoji.RAIN_DROP + "File cleaned out; "
//                            .concat(" deleted = " + deleted + " : ")
//                            .concat(file.getAbsolutePath()));
//                }
//            }
//        }
//    }
}
