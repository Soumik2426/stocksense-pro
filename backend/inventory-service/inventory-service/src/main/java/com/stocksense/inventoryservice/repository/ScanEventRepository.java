package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.ScanEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScanEventRepository
        extends JpaRepository<ScanEvent, Long> {

    Optional<ScanEvent> findByTenantIdAndIdempotencyKey(
            Long tenantId,
            String idempotencyKey);
}
