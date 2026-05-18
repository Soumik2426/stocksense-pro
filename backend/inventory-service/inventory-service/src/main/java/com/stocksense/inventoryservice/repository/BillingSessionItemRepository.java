package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.BillingSessionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingSessionItemRepository
        extends JpaRepository<
        BillingSessionItem,
        Long> {
}