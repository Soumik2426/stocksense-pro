package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.BillingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingSessionRepository
        extends JpaRepository<BillingSession, Long> {

    Optional<BillingSession> findByTenantIdAndId(
            Long tenantId,
            Long id);
}