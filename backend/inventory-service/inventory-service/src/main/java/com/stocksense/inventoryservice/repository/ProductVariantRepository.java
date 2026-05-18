package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByTenantIdAndSku(
            Long tenantId,
            String sku);

    Optional<ProductVariant> findByTenantIdAndBarcode(
            Long tenantId,
            String barcode
    );

    Optional<ProductVariant> findByTenantIdAndManufacturerBarcode(
            Long tenantId,
            String manufacturerBarcode
    );

    List<ProductVariant> findByTenantIdAndProductId(
            Long tenantId,
            Long productId);

    Optional<ProductVariant> findByTenantIdAndId(
            Long tenantId,
            Long id);
}
