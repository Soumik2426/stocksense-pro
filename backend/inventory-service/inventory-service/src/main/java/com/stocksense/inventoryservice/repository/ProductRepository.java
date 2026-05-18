package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTenantIdAndIsActiveTrue(Long tenantId);
    List<Product> findByTenantIdAndNameContainingIgnoreCase(
            Long tenantId,
            String name);

    Page<Product> findByTenantIdAndCategoryIgnoreCase(
            Long tenantId,
            String category,
            Pageable pageable);

    Page<Product> findByTenantId(
            Long tenantId,
            Pageable pageable);

    Optional<Product> findByTenantIdAndId(
            Long tenantId,
            Long id);
}