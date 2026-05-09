package com.stocksense.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockResponse {

    private Long variantId;

    private Long availableQuantity;
}