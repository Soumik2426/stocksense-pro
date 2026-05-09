package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.entity.*;
import com.stocksense.inventoryservice.exception.DuplicateTransactionException;
import com.stocksense.inventoryservice.exception.InsufficientStockException;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.InventoryTransactionRepository;
import com.stocksense.inventoryservice.repository.ProductVariantRepository;
import com.stocksense.inventoryservice.repository.StockLedgerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockLedgerRepository stockLedgerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Override
    @Transactional
    public void restock(Long variantId,
                        Long quantity,
                        String transactionId,
                        String idempotencyKey) {

        validateDuplicateEvent(idempotencyKey);

        ProductVariant variant = getVariant(variantId);

        StockLedger ledger = getOrCreateLedger(variant);

        ledger.setQuantity(ledger.getQuantity() + quantity);

        stockLedgerRepository.save(ledger);

        createTransaction(
                variant,
                TransactionType.RESTOCK,
                quantity,
                transactionId,
                idempotencyKey
        );
    }

    @Override
    @Transactional
    public void sale(Long variantId,
                     Long quantity,
                     String transactionId,
                     String idempotencyKey) {

        validateDuplicateEvent(idempotencyKey);

        ProductVariant variant = getVariant(variantId);

        StockLedger ledger = getOrCreateLedger(variant);

        if (ledger.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock");
        }

        ledger.setQuantity(ledger.getQuantity() - quantity);

        stockLedgerRepository.save(ledger);

        createTransaction(
                variant,
                TransactionType.SALE,
                quantity,
                transactionId,
                idempotencyKey
        );
    }

    @Override
    @Transactional
    public void refund(Long variantId,
                       Long quantity,
                       String transactionId,
                       String idempotencyKey) {

        validateDuplicateEvent(idempotencyKey);

        ProductVariant variant = getVariant(variantId);

        StockLedger ledger = getOrCreateLedger(variant);

        ledger.setQuantity(ledger.getQuantity() + quantity);

        stockLedgerRepository.save(ledger);

        createTransaction(
                variant,
                TransactionType.REFUND,
                quantity,
                transactionId,
                idempotencyKey
        );
    }

    // ---------------- PRIVATE HELPERS ----------------

    private ProductVariant getVariant(Long variantId) {

        return productVariantRepository.findById(variantId)
                .orElseThrow(() ->{throw new ResourceNotFoundException(
                        "Variant not found");
                });
    }

    private StockLedger getOrCreateLedger(ProductVariant variant) {

        return stockLedgerRepository.findByVariantId(variant.getId())
                .orElseGet(() -> {

                    StockLedger ledger = StockLedger.builder()
                            .variant(variant)
                            .quantity(0L)
                            .reservedQuantity(0L)
                            .build();

                    return stockLedgerRepository.save(ledger);
                });
    }

    private void validateDuplicateEvent(String idempotencyKey) {

        boolean exists = inventoryTransactionRepository
                .findByIdempotencyKey(idempotencyKey)
                .isPresent();

        if (exists) {
            throw new DuplicateTransactionException(
                    "Duplicate transaction detected");
        }
    }

    private void createTransaction(ProductVariant variant,
                                   TransactionType type,
                                   Long quantity,
                                   String transactionId,
                                   String idempotencyKey) {

        InventoryTransaction transaction =
                InventoryTransaction.builder()
                        .variant(variant)
                        .transactionType(type)
                        .quantity(quantity)
                        .transactionId(transactionId)
                        .idempotencyKey(idempotencyKey)
                        .build();

        inventoryTransactionRepository.save(transaction);
    }
}