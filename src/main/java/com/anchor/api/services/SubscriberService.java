package com.anchor.api.services;

import com.anchor.api.util.E;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {
    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriberService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    public SubscriberService() {
        LOGGER.info(E.FROG.concat(E.FROG).concat("Subscriber Service is up ".concat(E.FROG.concat(E.FROG))));
    }

    // use the default project id
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            LOGGER.info(E.FROG.concat(E.FROG).concat(
                    "receiveMessage Message Id: " + message.getMessageId() + " Data: " + message.getData().toStringUtf8()));
            // Ack only after all work for the message is complete.
            consumer.ack();
        }
    }

    public void subscribe(String subscriptionId) {

        LOGGER.info(E.FROG.concat(E.FROG).concat("SubscriberService: subscribe ... ".concat(E.FOX)));

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(PROJECT_ID, subscriptionId);
        Subscriber subscriber;
        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            // Allow the subscriber to run indefinitely unless an unrecoverable error occurs.
            subscriber.awaitTerminated();
        } catch (IllegalStateException e) {
            System.out.println("Subscriber unexpectedly stopped: " + e);
        }
    }
}