package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.StoreCategory;
import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_description", columnDefinition = "TEXT")
    private String storeDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_category", nullable = false)
    private StoreCategory storeCategory;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "monthly_sales_estimate", nullable = false)
    private int monthlySalesEstimate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = StoreRequestStatus.PENDIENTE;
    }
}
