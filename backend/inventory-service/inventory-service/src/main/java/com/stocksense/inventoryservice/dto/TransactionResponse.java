package com.stocksense.inventoryservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {

    private Long transactionId;

    private String transactionType;

    private Long quantity;

    private String externalTransactionId;

    private String idempotencyKey;

    private LocalDateTime createdAt;
}