package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.kafka.InventoryEvent;
import com.stocksense.inventoryservice.kafka.InventoryProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final InventoryProducer
            inventoryProducer;

    @PostMapping("/send")
    public String sendEvent(
            @RequestBody InventoryEvent event) {

        inventoryProducer
                .sendInventoryEvent(event);

        return "Kafka event sent successfully";
    }
}