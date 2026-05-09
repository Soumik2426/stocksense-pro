package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.ProductResponse;
import com.stocksense.inventoryservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.stocksense.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    // Create Product
    @PostMapping
    public Product createProduct(@RequestBody Product product) {

        return productRepository.save(product);
    }

    // Get all active products by tenant
    @GetMapping("/tenant/{tenantId}")
    public List<Product> getProductsByTenant(
            @PathVariable Long tenantId) {

        return productRepository
                .findByTenantIdAndIsActiveTrue(tenantId);
    }

    // Search products by name
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(
            @RequestParam String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> ProductResponse.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .category(product.getCategory())
                        .build())
                .toList();
    }

    @GetMapping
    public Page<ProductResponse> getProducts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return productRepository
                .findAll(pageable)
                .map(product ->
                        ProductResponse.builder()
                                .productId(product.getId())
                                .name(product.getName())
                                .category(product.getCategory())
                                .build()
                );
    }

    // Get products by category with pagination
    @GetMapping("/category/{category}")
    public Page<ProductResponse>
    getProductsByCategory(

            @PathVariable String category,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return productRepository
                .findByCategoryIgnoreCase(
                        category,
                        pageable
                )
                .map(product ->
                        ProductResponse.builder()
                                .productId(product.getId())
                                .name(product.getName())
                                .category(product.getCategory())
                                .build()
                );
    }
}