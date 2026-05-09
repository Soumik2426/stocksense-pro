package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.LowStockResponse;
import com.stocksense.inventoryservice.dto.StockResponse;
import com.stocksense.inventoryservice.entity.StockLedger;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.StockLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockLedgerRepository
            stockLedgerRepository;

    @GetMapping("/variant/{variantId}")
    public StockResponse getStock(
            @PathVariable Long variantId) {

        StockLedger ledger =
                stockLedgerRepository
                        .findByVariantId(variantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stock not found"));

        return new StockResponse(
                variantId,
                ledger.getQuantity()
        );
    }

    // Low Stock Alert
    @GetMapping("/low-stock")
    public List<LowStockResponse> getLowStockItems(

            @RequestParam(defaultValue = "5")
            Long threshold
    ) {

        return stockLedgerRepository
                .findByQuantityLessThanEqual(threshold)
                .stream()
                .map(ledger ->
                        LowStockResponse.builder()
                                .variantId(
                                        ledger.getVariant().getId())
                                .productName(
                                        ledger.getVariant()
                                                .getProduct()
                                                .getName())
                                .sku(
                                        ledger.getVariant()
                                                .getSku())
                                .availableQuantity(
                                        ledger.getQuantity())
                                .build()
                )
                .toList();
    }
}