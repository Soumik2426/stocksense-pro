package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billing_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    private Long cashierId;

    private String counterId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(
            mappedBy = "billingSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BillingSessionItem> items =
            new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

                if (this.tenantId == null) {
                        throw new IllegalStateException(
                                        "tenantId is required"
                        );
                }

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        if (this.status == null) {
            this.status =
                    SessionStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt =
                LocalDateTime.now();
    }
}
