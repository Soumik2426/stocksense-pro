package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "store_configurations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_store_config_tenant",
                        columnNames = "tenantId"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreConfiguration {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    // GST / Tax
    @Column(nullable = false)
    private Boolean gstEnabled = false;

    @Column(
            precision = 5,
            scale = 2
    )
    private BigDecimal defaultTaxPercentage =
            BigDecimal.ZERO;

    // Khata
    @Column(nullable = false)
    private Boolean khataEnabled = true;

    // Bargaining / manual discounts
    @Column(nullable = false)
    private Boolean bargainingEnabled = true;

    // Split payment support
    @Column(nullable = false)
    private Boolean splitPaymentEnabled = true;

    // Invoice numbering
    @Column(nullable = false)
    private String invoicePrefix = "INV";

    // Currency
    @Column(nullable = false)
    private String currency = "INR";

    // Optional future flags
    @Column(nullable = false)
    private Boolean barcodeMandatory = true;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (tenantId == null) {
            throw new IllegalStateException(
                    "tenantId is required"
            );
        }

        if (gstEnabled == null) {
            gstEnabled = false;
        }

        if (defaultTaxPercentage == null) {
            defaultTaxPercentage =
                    BigDecimal.ZERO;
        }

        if (khataEnabled == null) {
            khataEnabled = true;
        }

        if (bargainingEnabled == null) {
            bargainingEnabled = true;
        }

        if (splitPaymentEnabled == null) {
            splitPaymentEnabled = true;
        }

        if (invoicePrefix == null) {
            invoicePrefix = "INV";
        }

        if (currency == null) {
            currency = "INR";
        }

        if (barcodeMandatory == null) {
            barcodeMandatory = true;
        }

        if (active == null) {
            active = true;
        }

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}