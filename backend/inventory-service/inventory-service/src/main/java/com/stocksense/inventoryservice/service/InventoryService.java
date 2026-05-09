package com.stocksense.inventoryservice.service;

public interface InventoryService {

    void restock(Long variantId,
                 Long quantity,
                 String transactionId,
                 String idempotencyKey);

    void sale(Long variantId,
              Long quantity,
              String transactionId,
              String idempotencyKey);

    void refund(Long variantId,
                Long quantity,
                String transactionId,
                String idempotencyKey);
}