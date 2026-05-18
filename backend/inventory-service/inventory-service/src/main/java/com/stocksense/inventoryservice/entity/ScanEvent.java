package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "scan_events",
        indexes = {
                @Index(name = "idx_scan_event_tenant_id", columnList = "tenantId"),
                @Index(name = "idx_scan_event_variant_id", columnList = "variant_id"),
                @Index(name = "idx_scan_event_idempotency_key", columnList = "idempotencyKey"),
                @Index(name = "idx_scan_event_transaction_id", columnList = "transactionId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private String barcode;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String status;

    private String eventSource;

    private LocalDateTime scannedAt;

    private LocalDateTime serverReceivedAt;

    @PrePersist
    public void prePersist() {
        if (this.variant != null
                && this.variant.getTenantId() != null) {
            if (this.tenantId == null) {
                this.tenantId = this.variant.getTenantId();
            } else if (!this.tenantId.equals(this.variant.getTenantId())) {
                throw new IllegalStateException(
                        "scan event tenant must match variant tenant"
                );
            }
        }

        if (this.tenantId == null) {
            throw new IllegalStateException(
                    "tenantId is required"
            );
        }

        if (this.scannedAt == null) {
            this.scannedAt = LocalDateTime.now();
        }

        this.serverReceivedAt = LocalDateTime.now();
    }
}
