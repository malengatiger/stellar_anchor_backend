package com.anchor.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;


@Component
public class EndpointsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsListener.class);
    int cnt = 0, mCount = 0;
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 EndpointsListener: handleContextRefresh fired! " +
                "\uD83E\uDD66 \uD83E\uDD66 \uD83E\uDD66 Listing all available endpoints");
       mCount = 0;
        List<String> posts = new ArrayList<>();
        List<String> gets = new ArrayList<>();
        List<String> other = new ArrayList<>();
        applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods().forEach((key, value) -> {
                    mCount++;
                    if (key.toString().contains("POST")) {
                        posts.add("POST ".concat(value.toString()));
                    } else {
                        if (key.toString().contains("GET")) {
                            gets.add("GET ".concat(value.toString()));
                        } else {
                            other.add(key.toString().concat(" ").concat(value.toString()));
                        }
                    }
//            LOGGER.info("\uD83C\uDF45 Anchor ENDPOINT #"+cnt+" \uD83C\uDF45 {} {}", key, value);
        });
        cnt = 0;
        LOGGER.info(E.FLOWER_YELLOW.concat("POST endpoints ..... ").concat(" : " + posts.size() + " ").concat(E.FLOWER_YELLOW));
        for (String post : posts) {
            cnt++;
            LOGGER.info(E.RED_APPLE.concat("Server POST ").concat(E.PEAR).concat("EndPoint").concat(" #" + cnt + " ").concat(post));
        }
        LOGGER.info(E.FLOWER_YELLOW.concat("GET endpoints ..... ").concat(" : " + gets.size() + " ").concat(E.FLOWER_YELLOW));
        cnt = 0;
        for (String gg : gets) {
            cnt++;
            LOGGER.info(E.RED_APPLE.concat("Server GET ").concat(E.BLUE_DOT).concat("EndPoint").concat(" #" + cnt + " ").concat(gg));
        }
        LOGGER.info(E.FLOWER_YELLOW.concat("OTHER endpoints ..... ").concat(" : " + other.size() + " ").concat(E.FLOWER_YELLOW));

        cnt = 0;
        for (String gg : other) {
            cnt++;
            LOGGER.info(E.RED_APPLE.concat("Server EndPoint ").concat(E.DICE).concat(" #" + cnt + " ").concat(gg));
        }
        LOGGER.info("\uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 \uD83C\uDF45 EndpointsListener: Total EndPoints: " + mCount);
    }
}
