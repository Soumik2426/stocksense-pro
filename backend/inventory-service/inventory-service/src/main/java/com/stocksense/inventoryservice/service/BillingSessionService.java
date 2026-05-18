package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.entity.BillingSession;
import com.stocksense.inventoryservice.entity.PaymentType;

public interface BillingSessionService {

    BillingSession createSession(
            Long tenantId
    );

    BillingSession scanItem(
            Long tenantId,
            Long sessionId,
            Long variantId
    );

    BillingSession completePayment(
            Long tenantId,
            Long sessionId,
            PaymentType paymentType
    );

    BillingSession cancelSession(
            Long tenantId,
            Long sessionId
    );
}
