package com.atomiczek.shoppinglist.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, UserIdMessage> userIdProducerFactory() {
        Map<String, Object> configProperties = createConfiguration();
        return new DefaultKafkaProducerFactory<>(configProperties);
    }

    private static Map<String, Object> createConfiguration() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProperties;
    }

    @Bean
    public KafkaTemplate<String, UserIdMessage> userIdKafkaTemplate() {
        return new KafkaTemplate<>(userIdProducerFactory());
    }

    @Bean
    public ProducerFactory<String, UserRemoveMessage> userRemoveProducerFactory() {
        Map<String, Object> configProperties = createConfiguration();
        return new DefaultKafkaProducerFactory<>(configProperties);
    }

    @Bean
    public KafkaTemplate<String, UserRemoveMessage> userRemoveKafkaTemplate() {
        return new KafkaTemplate<>(userRemoveProducerFactory());
    }
}
