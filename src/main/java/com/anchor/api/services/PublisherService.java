package com.anchor.api.services;


import com.anchor.api.util.E;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublisherService {

    // use the default project id
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    public static final Logger LOGGER = LoggerFactory.getLogger(PublisherService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public PublisherService() {
        LOGGER.info(E.CLOVER.concat(E.CLOVER.concat("Publisher Service constructed ".concat(E.YELLOW_BIRD))));
    }

    public void publish(String message, String topicId) throws Exception {
        LOGGER.info("\uD83D\uDE21 about to publish message: " + message .concat(" \uD83D\uDD35 \uD83D\uDD35 topicId: ".concat(topicId)));
        LOGGER.info("\uD83D\uDE21 publish message using projectId: ".concat(PROJECT_ID));
        int messageCount = 1;
        ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<>();
        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            ApiFuture<String> future = publisher.publish(pubsubMessage);
            futures.add(future);
        } finally {
            // Wait on any pending requests
            List<String> messageIds = ApiFutures.allAsList(futures).get();
            for (String messageId : messageIds) {
                LOGGER.info("\uD83D\uDE21 \uD83D\uDE21 PublisherService: MESSAGE published; messageId: "
                        + messageId + " - \uD83D\uDD35 \uD83D\uDD35 " + message);
            }
            if (publisher != null) {
                publisher.shutdown();
            }
        }
    }
}