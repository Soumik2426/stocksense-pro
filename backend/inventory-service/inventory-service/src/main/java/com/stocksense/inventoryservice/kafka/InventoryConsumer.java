package com.stocksense.inventoryservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocksense.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryService inventoryService;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @KafkaListener(
            topics = "inventory-events",
            groupId = "inventory-group"
    )
    public void consume(String message) {

        try {

            InventoryEvent event =
                    objectMapper.readValue(
                            message,
                            InventoryEvent.class
                    );

            System.out.println(
                    "Kafka message received: "
                            + event.getOperation()
            );

            switch (event.getOperation()) {

                case "SALE" -> inventoryService.sale(
                        event.getVariantId(),
                        event.getQuantity(),
                        event.getTransactionId(),
                        event.getIdempotencyKey()
                );

                case "RESTOCK" ->
                        inventoryService.restock(
                                event.getVariantId(),
                                event.getQuantity(),
                                event.getTransactionId(),
                                event.getIdempotencyKey()
                        );

                case "REFUND" ->
                        inventoryService.refund(
                                event.getVariantId(),
                                event.getQuantity(),
                                event.getTransactionId(),
                                event.getIdempotencyKey()
                        );

                default -> System.out.println(
                        "Unknown operation received"
                );
            }

            System.out.println(
                    "Inventory updated from Kafka"
            );

        } catch (Exception e) {

            System.out.println(
                    "Kafka processing failed"
            );

            throw new RuntimeException(e);
        }
    }
}