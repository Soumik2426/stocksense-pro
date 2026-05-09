package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.VariantResponse;
import com.stocksense.inventoryservice.entity.ProductVariant;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantRepository productVariantRepository;

    // Create Variant
    @PostMapping
    public VariantResponse createVariant(
            @RequestBody ProductVariant variant) {

        ProductVariant savedVariant =
                productVariantRepository.save(variant);

        return mapToResponse(savedVariant);
    }

    // Get variants of product
    @GetMapping("/product/{productId}")
    public List<VariantResponse> getVariantsByProduct(
            @PathVariable Long productId) {

        return productVariantRepository
                .findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //Get variant by barcode
    @GetMapping("/barcode/{barcode}")
    public VariantResponse getByBarcode(
            @PathVariable String barcode) {

        ProductVariant variant =
                productVariantRepository
                        .findByBarcode(barcode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found"));

        return mapToResponse(variant);
    }

    //Get variant by SKU
    @GetMapping("/sku/{sku}")
    public VariantResponse getBySku(
            @PathVariable String sku) {

        ProductVariant variant =
                productVariantRepository
                        .findBySku(sku)
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