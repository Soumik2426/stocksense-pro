package com.stocksense.inventoryservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSearchResponse {

    private Long customerId;

    private String name;

    private String phone;

    private BigDecimal currentDue;
}