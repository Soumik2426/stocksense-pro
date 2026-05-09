package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.TransactionResponse;
import com.stocksense.inventoryservice.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final InventoryTransactionRepository
            transactionRepository;

    @GetMapping("/variant/{variantId}")
    public List<TransactionResponse>
    getTransactionsByVariant(

            @PathVariable Long variantId
    ) {

        return transactionRepository
                .findByVariantIdOrderByCreatedAtDesc(
                        variantId
                )
                .stream()
                .map(transaction ->
                        TransactionResponse.builder()
                                .transactionId(
                                        transaction.getId())
                                .transactionType(
                                        transaction
                                                .getTransactionType()
                                                .name())
                                .quantity(
                                        transaction.getQuantity())
                                .externalTransactionId(
                                        transaction
                                                .getTransactionId())
                                .idempotencyKey(
                                        transaction
                                                .getIdempotencyKey())
                                .createdAt(
                                        transaction
                                                .getCreatedAt())
                                .build()
                )
                .toList();
    }
}