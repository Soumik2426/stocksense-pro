package com.stocksense.inventoryservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocksense.inventoryservice.dto.ScannerInventoryRequest;
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

                case "SALE", "RESTOCK", "REFUND" -> {

                                        if (event.getTenantId() == null) {
                                                throw new IllegalArgumentException(
                                                                "tenantId is required"
                                                );
                                        }

                    if (event.getBarcode() != null
                                                        ) {

                        ScannerInventoryRequest request =
                                new ScannerInventoryRequest();
                        request.setTenantId(event.getTenantId());
                        request.setBarcode(event.getBarcode());
                        request.setOperation(event.getOperation());
                        request.setQuantity(event.getQuantity());
                        request.setTransactionId(
                                event.getTransactionId()
                        );
                        request.setIdempotencyKey(
                                event.getIdempotencyKey()
                        );
                        request.setEventSource("KAFKA");

                        inventoryService.processScannerEvent(
                                request
                        );

                    } else if (event.getVariantId() == null) {
                        throw new IllegalArgumentException(
                                "variantId is required"
                        );

                    } else if ("SALE".equals(
                            event.getOperation())) {
                        inventoryService.sale(
                                event.getTenantId(),
                                event.getVariantId(),
                                event.getQuantity(),
                                event.getTransactionId(),
                                event.getIdempotencyKey()
                        );

                    } else if ("RESTOCK".equals(
                            event.getOperation())) {
                        inventoryService.restock(
                                event.getTenantId(),
                                event.getVariantId(),
                                event.getQuantity(),
                                event.getTransactionId(),
                                event.getIdempotencyKey()
                        );

                    } else {
                        inventoryService.refund(
                                                                event.getTenantId(),
                                event.getVariantId(),
                                event.getQuantity(),
                                event.getTransactionId(),
                                event.getIdempotencyKey()
                        );
                    }
                }

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
