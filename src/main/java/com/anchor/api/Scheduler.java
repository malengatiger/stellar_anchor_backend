package com.anchor.api;


import com.anchor.api.controllers.ClientController;
import com.anchor.api.data.anchor.Agent;
import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.services.TOMLService;
import com.anchor.api.util.Emoji;
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
public class Scheduler {
    public static final Logger LOGGER = Logger.getLogger(Scheduler.class.getSimpleName());
    @Autowired
    private ApplicationContext context;
    @Value("${anchorName}")
    private String anchorName;
    @Autowired
    private TOMLService tomlService;

    public Scheduler() {
        LOGGER.info(Emoji.YELLOW_BIRD.concat(Emoji.YELLOW_BIRD) +
                "Scheduler constructed. Waiting to be triggered ".concat(Emoji.YELLOW_BIRD));
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)
//    @Scheduled(fixedRate = 1000 * 15)
    public void fixedRateScheduled() throws Exception {
        LOGGER.info(Emoji.PRETZEL.concat(Emoji.PRETZEL).concat(Emoji.PRETZEL) + "Fixed Rate scheduler; " +
                "\uD83C\uDF3C CALCULATE LOAN BALANCES or OTHER NECESSARY WORK: " + new DateTime().toDateTimeISO().toString()
                + " " + Emoji.RED_APPLE);
//        try {
//            fileCleanUp();
//        } catch (Exception e) {
//            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK) + "File CleanUp fell down");
//            e.printStackTrace();
//        }
        try {

            FirebaseService firebaseService = context.getBean(FirebaseService.class);
            List<Anchor> anchors = firebaseService.getAnchors();
            if (!anchors.isEmpty()) {
                Anchor anchor = anchors.get(0);

                List<Agent> list = firebaseService.getAgents(anchor.getAnchorId());
                for (Agent agent : list) {
                    LOGGER.info(Emoji.DICE.concat(Emoji.DICE) + "Agent: ".concat(agent.getFullName()).concat(" ")
                            .concat(Emoji.HEART_BLUE));
                }
                Toml toml = tomlService.getAnchorToml(anchor.getAnchorId());
                if (toml != null) {
                    LOGGER.info(Emoji.DICE.concat(Emoji.DICE) + "  ANCHOR TOML FILE " + Emoji.RED_APPLE + Emoji.RED_APPLE);
                    LOGGER.info("\n" + Emoji.DICE.concat(Emoji.DICE) + toml.toMap().toString() + Emoji.DICE.concat(Emoji.DICE));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(Emoji.NOT_OK.concat(Emoji.NOT_OK) + "Firebase query or anchor toml retrieval fell down");
//            e.printStackTrace();
        }
    }

    public void fileCleanUp() {

        File directory = new File("/");
        File test = new File(directory,"newFile.txt");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C test file: "+  test.getAbsolutePath() +
                "  \uD83D\uDC99 \uD83D\uDC9C");
        if (directory.exists()) {
            LOGGER.info(Emoji.HASH.concat(Emoji.HASH).concat(Emoji.HASH)
                    + "Directory ".concat(directory.getAbsolutePath()));
            File[] list = directory.listFiles();
            if ( list != null){
                for (File file : list) {
                    LOGGER.info(Emoji.HASH.concat(Emoji.HASH) + "File ".concat(file.getAbsolutePath()));
                }
            }
        } else {
            LOGGER.info(Emoji.HASH.concat(Emoji.HASH).concat(Emoji.PEPPER).concat(Emoji.PEPPER) + "Directory "
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
