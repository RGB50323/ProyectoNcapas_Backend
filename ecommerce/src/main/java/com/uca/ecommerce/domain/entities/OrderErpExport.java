package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.ErpExportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_erp_exports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderErpExport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "erp_export_status", nullable = false)
    private ErpExportStatus erpExportStatus;

    @Column(name = "erp_reference", unique = true, length = 50)
    private String erpReference;

    @Column(name = "erp_exported_at")
    private LocalDateTime erpExportedAt;

    @Column(name = "erp_error_message", columnDefinition = "TEXT")
    private String erpErrorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (erpExportStatus == null) erpExportStatus = ErpExportStatus.PENDING_EXPORT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
