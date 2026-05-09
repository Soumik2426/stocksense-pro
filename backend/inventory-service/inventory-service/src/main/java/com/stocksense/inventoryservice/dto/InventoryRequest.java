package com.stocksense.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {

    @NotNull(message = "Variant ID is required")
    private Long variantId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1,
            message = "Quantity must be greater than 0")
    private Long quantity;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}