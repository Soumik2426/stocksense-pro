package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stock_ledger",
        indexes = {
                @Index(
                        name = "idx_variant_id",
                        columnList = "variant_id"
                ),
                @Index(
                        name = "idx_stock_tenant_id",
                        columnList = "tenantId"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLedger {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    // Which variant this stock belongs to
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            nullable = false,
            unique = true
    )
    private ProductVariant variant;

    @Column(nullable = false)
    private Long tenantId;

    // Total physical stock
    @Column(nullable = false)
    private Long totalQuantity = 0L;

    // Reserved during billing/cart flow
    @Column(nullable = false)
    private Long reservedQuantity = 0L;

    // Currently sellable stock
    @Column(nullable = false)
    private Long availableQuantity = 0L;

    // Optimistic locking
    @Version
    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (this.variant == null) {
            throw new IllegalStateException(
                    "variant is required"
            );
        }

        Long variantTenantId =
                this.variant.getTenantId();

        if (variantTenantId == null) {
            throw new IllegalStateException(
                    "variant tenantId is required"
            );
        }

        if (this.tenantId == null) {
            this.tenantId =
                    variantTenantId;
        } else if (!this.tenantId.equals(variantTenantId)) {
            throw new IllegalStateException(
                    "stock tenant must match variant tenant"
            );
        }

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        // Safety fallback
        if (this.totalQuantity == null) {
            this.totalQuantity = 0L;
        }

        if (this.reservedQuantity == null) {
            this.reservedQuantity = 0L;
        }

        this.availableQuantity =
                this.totalQuantity
                        - this.reservedQuantity;
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt =
                LocalDateTime.now();

        // Always maintain consistency
        this.availableQuantity =
                this.totalQuantity
                        - this.reservedQuantity;
    }
}
