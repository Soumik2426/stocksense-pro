package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer>
    findByTenantIdAndPhone(
            Long tenantId,
            String phone
    );

    List<Customer>
    findByTenantIdAndNameContainingIgnoreCase(
            Long tenantId,
            String name
    );

    Optional<Customer>
    findByTenantIdAndId(
            Long tenantId,
            Long customerId
    );

    List<Customer>
    findByTenantIdAndIsActiveTrue(
            Long tenantId
    );
}