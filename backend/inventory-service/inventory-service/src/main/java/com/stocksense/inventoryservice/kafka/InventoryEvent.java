package com.stocksense.inventoryservice.kafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {

    private Long variantId;

    private Long quantity;

    private String transactionId;

    private String idempotencyKey;

    private String operation;
}