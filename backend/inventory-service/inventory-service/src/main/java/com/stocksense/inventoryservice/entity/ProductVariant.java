package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_variants",
        indexes = {
                @Index(name = "idx_product_id", columnList = "product_id"),
                @Index(name = "idx_sku", columnList = "sku"),
                @Index(name = "idx_barcode", columnList = "barcode")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_variant_per_product",
                        columnNames = {"product_id", "attributes"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // SKU (global unique identifier)
    @Column(unique = true, nullable = false)
    private String sku;

    // Barcode (NOT globally unique)
    @Column
    private String barcode;

    // 🔥 Variant DNA (handles ALL your requirements)
    @Column(columnDefinition = "json", nullable = false)
    private String attributes;

    private Double price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}