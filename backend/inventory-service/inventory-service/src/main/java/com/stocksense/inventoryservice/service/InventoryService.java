package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.dto.ScannerInventoryRequest;

public interface InventoryService {

    void sale(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void reserveStock(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void confirmSale(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void releaseReservation(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void refund(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void restock(
            Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey
    );

    void processScannerEvent(
            ScannerInventoryRequest request
    );
}
