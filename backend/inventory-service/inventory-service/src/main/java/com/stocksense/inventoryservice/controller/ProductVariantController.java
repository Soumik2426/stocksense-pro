package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.VariantResponse;
import com.stocksense.inventoryservice.entity.PriceHistory;
import com.stocksense.inventoryservice.entity.Product;
import com.stocksense.inventoryservice.entity.ProductVariant;
import com.stocksense.inventoryservice.entity.StockLedger;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.PriceHistoryRepository;
import com.stocksense.inventoryservice.repository.ProductRepository;
import com.stocksense.inventoryservice.repository.ProductVariantRepository;
import com.stocksense.inventoryservice.repository.StockLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantRepository productVariantRepository;

    private final ProductRepository productRepository;

    private final StockLedgerRepository stockLedgerRepository;

    private final PriceHistoryRepository priceHistoryRepository;

    // Create Variant
    @PostMapping
    public VariantResponse createVariant(
            @RequestParam Long tenantId,
            @RequestBody ProductVariant variant) {

        if (variant.getProduct() == null
                || variant.getProduct().getId() == null) {
            throw new IllegalArgumentException(
                    "Product is required"
            );
        }

        Product product =
                productRepository
                        .findByTenantIdAndId(
                                tenantId,
                                variant.getProduct().getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                ));

        variant.setProduct(product);
        variant.setTenantId(tenantId);
        if (variant.getPrice() == null) {
            variant.setPrice(BigDecimal.ZERO);
        }

        ProductVariant savedVariant =
                productVariantRepository.save(variant);

        StockLedger stockLedger =
                StockLedger.builder()
                        .variant(savedVariant)
                        .tenantId(savedVariant.getTenantId())
                        .totalQuantity(0L)
                        .reservedQuantity(0L)
                        .availableQuantity(0L)
                        .build();

        stockLedgerRepository.save(stockLedger);

        PriceHistory priceHistory =
                PriceHistory.builder()
                        .variant(savedVariant)
                        .tenantId(savedVariant.getTenantId())
                        .price(savedVariant.getPrice())
                        .effectiveFrom(LocalDateTime.now())
                        .build();

        priceHistoryRepository.save(priceHistory);

        return mapToResponse(savedVariant);
    }

    // Get variants of product
    @GetMapping("/product/{productId}")
    public List<VariantResponse> getVariantsByProduct(
            @RequestParam Long tenantId,
            @PathVariable Long productId) {

        return productVariantRepository
                .findByTenantIdAndProductId(
                        tenantId,
                        productId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //Get variant by barcode
    @GetMapping("/barcode/{barcode}")
    public VariantResponse getByBarcode(
            @PathVariable String barcode,
            @RequestParam Long tenantId) {

        ProductVariant variant =
                productVariantRepository
                        .findByTenantIdAndBarcode(
                                tenantId,
                                barcode
                        )
                        .or(() ->
                                productVariantRepository
                                        .findByTenantIdAndManufacturerBarcode(
                                                tenantId,
                                                barcode
                                        ))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found"));

        return mapToResponse(variant);
    }

    //Get variant by SKU
    @GetMapping("/sku/{sku}")
    public VariantResponse getBySku(
            @RequestParam Long tenantId,
            @PathVariable String sku) {

        ProductVariant variant =
                productVariantRepository
                        .findByTenantIdAndSku(tenantId, sku)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found"));

        return mapToResponse(variant);
    }

    // Entity -> DTO mapper
    private VariantResponse mapToResponse(
            ProductVariant variant) {

        return VariantResponse.builder()
                .variantId(variant.getId())
                .productId(
                        variant.getProduct().getId())
                .productName(
                        variant.getProduct().getName())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .attributes(
                        variant.getAttributes())
                .price(variant.getPrice())
                .build();
    }
}
