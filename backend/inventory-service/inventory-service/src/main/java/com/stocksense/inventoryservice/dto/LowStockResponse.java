package com.stocksense.inventoryservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LowStockResponse {

    private Long variantId;

    private String productName;

    private String sku;

    private Long availableQuantity;
}