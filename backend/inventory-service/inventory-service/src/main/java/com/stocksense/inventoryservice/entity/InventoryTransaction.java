package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_transactions",
        indexes = {
                @Index(name = "idx_variant_id", columnList = "variant_id"),
                @Index(name = "idx_transaction_id", columnList = "transactionId"),
                @Index(name = "idx_idempotency_key", columnList = "idempotencyKey")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // SALE / REFUND / RESTOCK
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    // Quantity changed
    @Column(nullable = false)
    private Long quantity;

    // External billing/order reference
    @Column(nullable = false)
    private String transactionId;

    // Kafka duplicate protection
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    // Optional notes
    private String remarks;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}