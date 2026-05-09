package com.stocksense.inventoryservice.repository;

import java.util.List;
import com.stocksense.inventoryservice.entity.StockLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockLedgerRepository extends JpaRepository<StockLedger, Long> {

    Optional<StockLedger> findByVariantId(Long variantId);
    List<StockLedger>
    findByQuantityLessThanEqual(Long quantity);
}