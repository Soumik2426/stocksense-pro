package com.stocksense.inventoryservice.repository;

import java.util.*;
import com.stocksense.inventoryservice.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    Optional<InventoryTransaction> findByTenantIdAndIdempotencyKey(
            Long tenantId,
            String idempotencyKey
    );

    List<InventoryTransaction> findByTenantIdAndVariantIdOrderByCreatedAtDesc(
            Long tenantId,
            Long variantId);
}
