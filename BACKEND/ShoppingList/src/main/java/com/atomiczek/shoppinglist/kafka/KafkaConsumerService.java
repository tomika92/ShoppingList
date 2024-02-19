package com.atomiczek.shoppinglist.kafka;

import com.atomiczek.shoppinglist.Controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private String logMessage = "";

    private static final Logger logger = LogManager.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = {"userIdTopic", "userRemoveTopic"}, groupId = "myGroup")
    public void listenMultipleTopics(ConsumerRecord<String, String> record) {
        String value = record.value();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if ("userIdTopic".equals(record.topic())) {
                UserIdMessage userIdMessage = objectMapper.readValue(value, UserIdMessage.class);
                logMessage += userIdMessage;
            } else if ("userRemoveTopic".equals(record.topic())) {
                UserRemoveMessage userRemoveMessage = objectMapper.readValue(value, UserRemoveMessage.class);
                logMessage += userRemoveMessage;
                System.out.println(logMessage);
                logMessage="";
            }
        } catch (Exception e) {
            logger.error("Exception during kafka consumer listening: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
