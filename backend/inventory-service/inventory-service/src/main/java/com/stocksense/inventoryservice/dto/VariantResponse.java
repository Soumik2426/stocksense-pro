package com.stocksense.inventoryservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class VariantResponse {

    private Long variantId;

    private Long productId;

    private String productName;

    private String sku;

    private String barcode;

    private String attributes;

    private BigDecimal price;
}