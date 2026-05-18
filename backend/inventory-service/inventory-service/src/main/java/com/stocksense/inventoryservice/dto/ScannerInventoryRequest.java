package com.stocksense.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScannerInventoryRequest {

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotBlank(message = "Barcode is required")
    private String barcode;

    @NotBlank(message = "Operation is required")
    private String operation;

    @NotNull(message = "Quantity is required")
    @Min(value = 1,
            message = "Quantity must be greater than 0")
    private Long quantity;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    private Long actorId;

    private String eventSource;

    private LocalDateTime scannedAt;
}
