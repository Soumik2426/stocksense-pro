package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.StoreConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreConfigurationRepository
        extends JpaRepository<StoreConfiguration, Long> {

    Optional<StoreConfiguration>
    findByTenantId(
            Long tenantId
    );
}