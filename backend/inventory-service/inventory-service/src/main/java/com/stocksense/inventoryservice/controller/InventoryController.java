package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.ApiResponse;
import com.stocksense.inventoryservice.dto.ScannerInventoryRequest;
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

    @PostMapping("/scan")
    public ApiResponse<Void> processScannerEvent(
            @Valid @RequestBody ScannerInventoryRequest request) {

        inventoryService.processScannerEvent(request);

        return new ApiResponse<>(
                true,
                "Scanner event processed successfully",
                null
        );
    }

    // RESTOCK
    @PostMapping("/restock")
    public ApiResponse<Void> restock(
            @Valid @RequestBody InventoryRequest request) {

        inventoryService.restock(
                request.getTenantId(),
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
                request.getTenantId(),
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
                request.getTenantId(),
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
