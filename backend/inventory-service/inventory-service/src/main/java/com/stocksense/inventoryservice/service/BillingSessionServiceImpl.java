package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.entity.*;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.BillingSessionRepository;
import com.stocksense.inventoryservice.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingSessionServiceImpl
        implements BillingSessionService {

    private final BillingSessionRepository
            billingSessionRepository;

    private final ProductVariantRepository
            productVariantRepository;

    private final InventoryService
            inventoryService;

    @Override
    @Transactional
    public BillingSession createSession(
            Long tenantId) {

        if (tenantId == null) {
            throw new ResourceNotFoundException(
                    "Tenant is required"
            );
        }

        BillingSession session =
                BillingSession.builder()
                        .tenantId(tenantId)
                        .status(SessionStatus.ACTIVE)
                        .totalAmount(BigDecimal.ZERO)
                        .build();

        return billingSessionRepository
                .save(session);
    }

    @Override
    @Transactional
    public BillingSession scanItem(
            Long tenantId,
            Long sessionId,
            Long variantId) {

        BillingSession session =
                billingSessionRepository
                        .findByTenantIdAndId(
                                tenantId,
                                sessionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session not found"
                                ));

        ProductVariant variant =
                productVariantRepository
                        .findByTenantIdAndId(
                                tenantId,
                                variantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found"
                                ));

        BillingSessionItem existingItem =
                session.getItems()
                        .stream()
                        .filter(item ->
                                item.getVariant()
                                        .getId()
                                        .equals(variantId)
                        )
                        .findFirst()
                        .orElse(null);

        Long nextQuantity =
                existingItem == null
                        ? 1L
                        : existingItem.getQuantity() + 1;

        // Reserve stock
        inventoryService.reserveStock(
                tenantId,
                variantId,
                1L,
                "RESERVE-" + sessionId,
                "RESERVE-" + sessionId
                        + "-"
                        + variantId
                        + "-"
                        + nextQuantity
        );

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + 1
            );

            BigDecimal subtotal =
                    existingItem.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            existingItem.getQuantity()
                                    )
                            );

            existingItem.setSubtotal(subtotal);

        } else {

            BillingSessionItem item =
                    BillingSessionItem.builder()
                            .tenantId(session.getTenantId())
                            .billingSession(session)
                            .variant(variant)
                            .quantity(1L)
                            .unitPrice(
                                    variant.getPrice()
                            )
                            .subtotal(
                                    variant.getPrice()
                            )
                            .build();

            session.getItems().add(item);
        }

        BigDecimal total =
                session.getItems()
                        .stream()
                        .map(BillingSessionItem::getSubtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        session.setTotalAmount(total);

        return billingSessionRepository
                .save(session);
    }

    @Override
    @Transactional
    public BillingSession completePayment(
            Long tenantId,
            Long sessionId,
            PaymentType paymentType) {

        BillingSession session =
                billingSessionRepository
                        .findByTenantIdAndId(
                                tenantId,
                                sessionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session not found"
                                ));

        for (BillingSessionItem item :
                session.getItems()) {

            inventoryService.confirmSale(
                    tenantId,
                    item.getVariant().getId(),
                    item.getQuantity(),
                    "CONFIRM-" + sessionId,
                    "CONFIRM-" + sessionId
                            + "-"
                            + item.getVariant().getId()
            );
        }

        session.setPaymentType(
                paymentType
        );

        session.setStatus(
                SessionStatus.PAID
        );

        return billingSessionRepository
                .save(session);
    }

    @Override
    @Transactional
    public BillingSession cancelSession(
            Long tenantId,
            Long sessionId) {

        BillingSession session =
                billingSessionRepository
                        .findByTenantIdAndId(
                                tenantId,
                                sessionId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session not found"
                                ));

        for (BillingSessionItem item :
                session.getItems()) {

            inventoryService.releaseReservation(
                    tenantId,
                    item.getVariant().getId(),
                    item.getQuantity(),
                    "RELEASE-" + sessionId,
                    "RELEASE-" + sessionId
                            + "-"
                            + item.getVariant().getId()
            );
        }

        session.setStatus(
                SessionStatus.CANCELLED
        );

        return billingSessionRepository
                .save(session);
    }
}