package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_session_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSessionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_session_id")
    private BillingSession billingSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Long quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    public void prePersist() {
        if (this.billingSession == null) {
            throw new IllegalStateException(
                "billingSession is required"
            );
        }

        Long sessionTenantId =
            this.billingSession.getTenantId();

        if (sessionTenantId == null) {
            throw new IllegalStateException(
                "billingSession tenantId is required"
            );
        }

        if (this.variant != null
            && this.variant.getTenantId() != null
            && !sessionTenantId.equals(this.variant.getTenantId())) {
            throw new IllegalStateException(
                "billing session item tenant must match variant tenant"
            );
        }

        if (this.tenantId == null) {
            this.tenantId = sessionTenantId;
        } else if (!this.tenantId.equals(sessionTenantId)) {
            throw new IllegalStateException(
                "billing session item tenant must match session tenant"
            );
        }
    }
}
