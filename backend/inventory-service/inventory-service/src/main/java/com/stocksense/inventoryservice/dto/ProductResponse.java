package com.stocksense.inventoryservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private Long productId;

    private String name;

    private String category;
}