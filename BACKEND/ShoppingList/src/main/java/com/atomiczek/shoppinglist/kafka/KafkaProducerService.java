package com.atomiczek.shoppinglist.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserIdMessage> userIdKafkaTemplate;
    private final KafkaTemplate<String, UserRemoveMessage> userRemovalKafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, UserIdMessage> userIdKafkaTemplate, KafkaTemplate<String, UserRemoveMessage> userRemovalKafkaTemplate) {
        this.userIdKafkaTemplate = userIdKafkaTemplate;
        this.userRemovalKafkaTemplate = userRemovalKafkaTemplate;
    }

    public void sendUserIdMessage(UUID userId) {
        UserIdMessage userIdMessage = new UserIdMessage(userId);
        userIdKafkaTemplate.send("userIdTopic", userIdMessage);
    }

    public void sendUserRemoveMessage(String message) {
        UserRemoveMessage userRemoveMessage = new UserRemoveMessage(message);
        userRemovalKafkaTemplate.send("userRemoveTopic", userRemoveMessage);
    }
}