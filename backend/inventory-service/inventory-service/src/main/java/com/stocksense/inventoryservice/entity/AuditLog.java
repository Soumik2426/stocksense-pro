package com.stocksense.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_log",
        indexes = {
                @Index(name = "idx_audit_tenant_id", columnList = "tenantId"),
                @Index(name = "idx_audit_entity", columnList = "entityType,entityId"),
                @Index(name = "idx_audit_idempotency_key", columnList = "idempotencyKey")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    private Long actorId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(columnDefinition = "json")
    private String oldValue;

    @Column(columnDefinition = "json")
    private String newValue;

    private String idempotencyKey;

    private String remarks;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
