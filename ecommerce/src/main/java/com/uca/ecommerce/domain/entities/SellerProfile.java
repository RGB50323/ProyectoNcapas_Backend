package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.StoreCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seller_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "store_name", nullable = false, unique = true)
    private String storeName;

    @Column(name = "store_description", columnDefinition = "TEXT")
    private String storeDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_category")
    private StoreCategory storeCategory;

    @Column(name = "location")
    private String location;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_sales", nullable = false)
    private int totalSales;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        totalSales = 0;
        verified = false;
    }

}