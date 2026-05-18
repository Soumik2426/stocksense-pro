package com.stocksense.inventoryservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long customerId;

    private String name;

    private String phone;

    private String address;

    private String email;

    private BigDecimal currentDue;

    private BigDecimal creditLimit;

    private Boolean isActive;
}