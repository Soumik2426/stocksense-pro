package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "customers",
        indexes = {
                @Index(
                        name = "idx_customer_tenant_id",
                        columnList = "tenantId"
                ),
                @Index(
                        name = "idx_customer_phone",
                        columnList = "phone"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_phone_tenant",
                        columnNames = {
                                "tenantId",
                                "phone"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    private String address;

    private String email;

    // Live khata balance
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal currentDue =
            BigDecimal.ZERO;

    // Max allowed due
    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal creditLimit =
            BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (this.tenantId == null) {
            throw new IllegalStateException(
                    "tenantId is required"
            );
        }

        if (this.currentDue == null) {
            this.currentDue =
                    BigDecimal.ZERO;
        }

        if (this.creditLimit == null) {
            this.creditLimit =
                    BigDecimal.ZERO;
        }

        if (this.isActive == null) {
            this.isActive = true;
        }

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt =
                LocalDateTime.now();
    }
}