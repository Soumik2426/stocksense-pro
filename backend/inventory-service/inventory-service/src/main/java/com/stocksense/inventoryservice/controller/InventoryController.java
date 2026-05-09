package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.ApiResponse;
import jakarta.validation.Valid;
import com.stocksense.inventoryservice.dto.InventoryRequest;
import com.stocksense.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // RESTOCK
    @PostMapping("/restock")
    public ApiResponse<Void> restock(
            @Valid @RequestBody InventoryRequest request) {

        inventoryService.restock(
                request.getVariantId(),
                request.getQuantity(),
                request.getTransactionId(),
                request.getIdempotencyKey()
        );

        return new ApiResponse<>(
                true,
                "Stock restock successfully",
                null
        );
    }

    // SALE
    @PostMapping("/sale")
    public ApiResponse<Void> sale(
            @Valid @RequestBody InventoryRequest request) {

        inventoryService.sale(
                request.getVariantId(),
                request.getQuantity(),
                request.getTransactionId(),
                request.getIdempotencyKey()
        );

        return new ApiResponse<>(
                true,
                "Sale completed successfully",
                null
        );
    }

    // REFUND
    @PostMapping("/refund")
    public ApiResponse<Void> refund(
            @Valid @RequestBody InventoryRequest request) {

        inventoryService.refund(
                request.getVariantId(),
                request.getQuantity(),
                request.getTransactionId(),
                request.getIdempotencyKey()
        );

        return new ApiResponse<>(
                true,
                "Refund completed successfully",
                null
        );
    }
}