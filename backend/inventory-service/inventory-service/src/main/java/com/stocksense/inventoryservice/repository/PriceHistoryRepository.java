package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceHistoryRepository
        extends JpaRepository<PriceHistory, Long> {

    Optional<PriceHistory> findFirstByTenantIdAndVariantIdAndEffectiveToIsNullOrderByEffectiveFromDesc(
            Long tenantId,
            Long variantId);
}
