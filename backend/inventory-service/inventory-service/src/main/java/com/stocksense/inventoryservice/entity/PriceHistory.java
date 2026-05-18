package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "price_history",
        indexes = {
                @Index(name = "idx_price_history_tenant_id", columnList = "tenantId"),
                @Index(name = "idx_price_history_variant_id", columnList = "variant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

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
            this.tenantId = variantTenantId;
        } else if (!this.tenantId.equals(variantTenantId)) {
            throw new IllegalStateException(
                    "price history tenant must match variant tenant"
            );
        }

        if (this.effectiveFrom == null) {
            this.effectiveFrom = LocalDateTime.now();
        }
    }
}
