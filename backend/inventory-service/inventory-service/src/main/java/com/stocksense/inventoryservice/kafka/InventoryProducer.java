package com.stocksense.inventoryservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, String>
            kafkaTemplate;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public void sendInventoryEvent(
            InventoryEvent event) {

        try {

            String jsonMessage =
                    objectMapper.writeValueAsString(
                            event
                    );

            kafkaTemplate.send(
                    "inventory-events",
                    jsonMessage
            );

            System.out.println(
                    "Inventory event sent: "
                            + jsonMessage
            );

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize event"
            );
        }
    }
}