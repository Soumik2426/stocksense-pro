package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.dto.ScannerInventoryRequest;
import com.stocksense.inventoryservice.entity.*;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl
        implements InventoryService {

    private final ProductVariantRepository productVariantRepository;

    private final StockLedgerRepository stockLedgerRepository;

    private final InventoryTransactionRepository
            inventoryTransactionRepository;

    private final ScanEventRepository scanEventRepository;

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void sale(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.SALE,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "API"
        );
    }

    @Override
    @Transactional
    public void reserveStock(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.RESERVE_STOCK,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "BILLING"
        );
    }

    @Override
    @Transactional
    public void confirmSale(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.CONFIRM_SALE,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "BILLING"
        );
    }

    @Override
    @Transactional
    public void releaseReservation(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.RELEASE_RESERVATION,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "BILLING"
        );
    }

    @Override
    @Transactional
    public void refund(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.REFUND,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "API"
        );
    }

    @Override
    @Transactional
    public void restock(
                        Long tenantId,
            Long variantId,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

                ProductVariant variant = getVariant(tenantId, variantId);

        applyStockChange(
                variant,
                TransactionType.RESTOCK,
                quantity,
                transactionId,
                idempotencyKey,
                null,
                "API"
        );
    }

    @Override
    @Transactional
    public void processScannerEvent(
            ScannerInventoryRequest request) {

        ProductVariant variant =
                productVariantRepository
                        .findByTenantIdAndBarcode(
                                request.getTenantId(),
                                request.getBarcode()
                        )
                        .or(() ->
                                productVariantRepository
                                        .findByTenantIdAndManufacturerBarcode(
                                                request.getTenantId(),
                                                request.getBarcode()
                                        ))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found for barcode"
                                ));

        TransactionType operation =
                TransactionType.valueOf(
                        request.getOperation()
                                .trim()
                                .toUpperCase(Locale.ROOT)
                );

        applyStockChange(
                variant,
                operation,
                request.getQuantity(),
                request.getTransactionId(),
                request.getIdempotencyKey(),
                request.getScannedAt(),
                request.getEventSource() == null
                        ? "SCANNER"
                        : request.getEventSource()
        );
    }

    private ProductVariant getVariant(
            Long tenantId,
            Long variantId) {

        if (tenantId == null) {
            throw new ResourceNotFoundException(
                    "Variant not found"
            );
        }

        return productVariantRepository
                .findByTenantIdAndId(
                        tenantId,
                        variantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant not found"
                        ));
    }

    private void applyStockChange(
            ProductVariant variant,
            TransactionType transactionType,
            Long quantity,
            String transactionId,
            String idempotencyKey,
            LocalDateTime scannedAt,
            String eventSource) {

        Long tenantId = variant.getTenantId();

        if (inventoryTransactionRepository
                .findByTenantIdAndIdempotencyKey(
                        tenantId,
                        idempotencyKey
                )
                .isPresent()) {

            return;
        }

        int retryCount = 0;

        while (retryCount < 3) {

            try {

                StockLedger stock =
                        getStockLedgerForOperation(
                                variant,
                                transactionType
                        );

                Long oldTotal =
                        stock.getTotalQuantity();

                Long oldReserved =
                        stock.getReservedQuantity();

                switch (transactionType) {

                    case SALE -> {
                        ensureAvailableStock(
                                stock,
                                quantity
                        );
                        stock.setTotalQuantity(
                                stock.getTotalQuantity()
                                        - quantity
                        );
                    }

                    case RESTOCK, REFUND -> stock.setTotalQuantity(
                            stock.getTotalQuantity()
                                    + quantity
                    );

                    case RESERVE_STOCK -> {
                        ensureAvailableStock(
                                stock,
                                quantity
                        );
                        stock.setReservedQuantity(
                                stock.getReservedQuantity()
                                        + quantity
                        );
                    }

                    case CONFIRM_SALE -> {
                        if (stock.getReservedQuantity()
                                < quantity) {
                            throw new RuntimeException(
                                    "Reserved stock is insufficient"
                            );
                        }
                        stock.setTotalQuantity(
                                stock.getTotalQuantity()
                                        - quantity
                        );
                        stock.setReservedQuantity(
                                stock.getReservedQuantity()
                                        - quantity
                        );
                    }

                    case RELEASE_RESERVATION -> {
                        if (stock.getReservedQuantity()
                                < quantity) {
                            throw new RuntimeException(
                                    "Reserved stock is insufficient"
                            );
                        }
                        stock.setReservedQuantity(
                                stock.getReservedQuantity()
                                        - quantity
                        );
                    }

                    default -> throw new RuntimeException(
                            "Unsupported inventory operation"
                    );
                }

                stockLedgerRepository.save(stock);

                saveTransaction(
                        variant,
                        transactionType,
                        quantity,
                        transactionId,
                        idempotencyKey
                );

                saveScanEvent(
                        variant,
                        transactionType,
                        quantity,
                        transactionId,
                        idempotencyKey,
                        scannedAt,
                        eventSource
                );

                saveAuditLog(
                        variant,
                        stock,
                        transactionType,
                        oldTotal,
                        oldReserved,
                        idempotencyKey
                );

                return;

            } catch (
                    ObjectOptimisticLockingFailureException e) {

                retryCount++;
            }
        }

        throw new RuntimeException(
                "Failed after retries due to concurrency conflict"
        );
    }

    private StockLedger getStockLedgerForOperation(
            ProductVariant variant,
            TransactionType transactionType) {

        return stockLedgerRepository
                .findByTenantIdAndVariantId(
                        variant.getTenantId(),
                        variant.getId()
                )
                .orElseGet(() -> {

                    if (transactionType != TransactionType.RESTOCK
                            && transactionType != TransactionType.REFUND) {
                        throw new ResourceNotFoundException(
                                "Stock not found"
                        );
                    }

                    return StockLedger.builder()
                            .variant(variant)
                            .tenantId(variant.getTenantId())
                            .totalQuantity(0L)
                            .reservedQuantity(0L)
                            .availableQuantity(0L)
                            .build();
                });
    }

    private void ensureAvailableStock(
            StockLedger stock,
            Long quantity) {

        if (stock.getAvailableQuantity()
                < quantity) {
            throw new RuntimeException(
                    "Insufficient stock"
            );
        }
    }

    private void saveTransaction(
            ProductVariant variant,
            TransactionType transactionType,
            Long quantity,
            String transactionId,
            String idempotencyKey) {

        InventoryTransaction transaction =
                InventoryTransaction.builder()
                        .variant(variant)
                        .tenantId(variant.getTenantId())
                        .quantity(quantity)
                        .transactionType(transactionType)
                        .transactionId(transactionId)
                        .idempotencyKey(idempotencyKey)
                        .remarks(transactionType.name())
                        .build();

        inventoryTransactionRepository
                .save(transaction);
    }

    private void saveScanEvent(
            ProductVariant variant,
            TransactionType transactionType,
            Long quantity,
            String transactionId,
            String idempotencyKey,
            LocalDateTime scannedAt,
            String eventSource) {

        ScanEvent scanEvent =
                ScanEvent.builder()
                        .tenantId(variant.getTenantId())
                        .variant(variant)
                        .barcode(variant.getBarcode())
                        .operation(transactionType.name())
                        .quantity(quantity)
                        .idempotencyKey(idempotencyKey)
                        .transactionId(transactionId)
                        .status("PROCESSED")
                        .eventSource(eventSource)
                        .scannedAt(scannedAt)
                        .build();

        scanEventRepository.save(scanEvent);
    }

    private void saveAuditLog(
            ProductVariant variant,
            StockLedger stock,
            TransactionType transactionType,
            Long oldTotal,
            Long oldReserved,
            String idempotencyKey) {

        String oldValue =
                "{\"totalQuantity\":"
                        + oldTotal
                        + ",\"reservedQuantity\":"
                        + oldReserved
                        + "}";

        String newValue =
                "{\"totalQuantity\":"
                        + stock.getTotalQuantity()
                        + ",\"reservedQuantity\":"
                        + stock.getReservedQuantity()
                        + "}";

        AuditLog auditLog =
                AuditLog.builder()
                        .tenantId(variant.getTenantId())
                        .action(transactionType.name())
                        .entityType("STOCK_LEDGER")
                        .entityId(stock.getId())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .idempotencyKey(idempotencyKey)
                        .remarks("Inventory stock change")
                        .build();

        auditLogRepository.save(auditLog);
    }
}
