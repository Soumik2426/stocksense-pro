package com.stocksense.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone is required")
    private String phone;

    private String address;

    private String email;

    private BigDecimal openingDue;

    private BigDecimal creditLimit;
}