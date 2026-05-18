package com.stocksense.inventoryservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterConsumer {

    @KafkaListener(
            topics = "inventory-events-dlt",
            groupId = "dlt-group"
    )
    public void consumeDeadLetter(
            String message) {

        System.out.println(
                "DLT EVENT RECEIVED: "
                        + message
        );
    }
}