package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {
}
