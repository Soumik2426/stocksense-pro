package com.stocksense.inventoryservice.repository;

import com.stocksense.inventoryservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTenantIdAndIsActiveTrue(Long tenantId);
    List<Product> findByNameContainingIgnoreCase(
            String name);
    Page<Product> findByCategoryIgnoreCase(
            String category,
            Pageable pageable);
}