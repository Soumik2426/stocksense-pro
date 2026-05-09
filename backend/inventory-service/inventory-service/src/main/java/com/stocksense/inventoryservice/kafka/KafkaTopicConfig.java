package com.stocksense.inventoryservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inventoryTopic() {

        return new NewTopic(
                "inventory-events",
                1,
                (short) 1
        );
    }
}