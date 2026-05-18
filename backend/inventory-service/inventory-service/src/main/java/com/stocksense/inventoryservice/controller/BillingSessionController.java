package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.entity.BillingSession;
import com.stocksense.inventoryservice.entity.PaymentType;
import com.stocksense.inventoryservice.service.BillingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingSessionController {

    private final BillingSessionService
            billingSessionService;

    @PostMapping("/session")
    public BillingSession createSession(
            @RequestParam
            Long tenantId) {

        return billingSessionService
                .createSession(tenantId);
    }

    @PostMapping("/{sessionId}/scan/{variantId}")
    public BillingSession scanItem(
            @RequestParam Long tenantId,
            @PathVariable Long sessionId,
            @PathVariable Long variantId) {

        return billingSessionService
                .scanItem(
                        tenantId,
                        sessionId,
                        variantId
                );
    }

    @PostMapping("/{sessionId}/complete")
    public BillingSession completePayment(
            @RequestParam Long tenantId,
            @PathVariable Long sessionId,
            @RequestParam PaymentType paymentType) {

        return billingSessionService
                .completePayment(
                        tenantId,
                        sessionId,
                        paymentType
                );
    }

    @PostMapping("/{sessionId}/cancel")
    public BillingSession cancelSession(
            @RequestParam Long tenantId,
            @PathVariable Long sessionId) {

        return billingSessionService
                .cancelSession(
                        tenantId,
                        sessionId
                );
    }
}
