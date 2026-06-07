package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_check", nullable = false)
    private VerificationStageStatus materialCheck;

    @Enumerated(EnumType.STRING)
    @Column(name = "construction_check", nullable = false)
    private VerificationStageStatus constructionCheck;

    @Enumerated(EnumType.STRING)
    @Column(name = "factory_code_check", nullable = false)
    private VerificationStageStatus factoryCodeCheck;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_inspection", nullable = false)
    private VerificationStageStatus finalInspection;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}