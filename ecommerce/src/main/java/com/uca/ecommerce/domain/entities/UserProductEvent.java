package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.ProductEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_product_events",
        indexes = {
                @Index(name = "idx_user_product_event_user", columnList =
                        "user_id"),
                @Index(name = "idx_user_product_event_product", columnList =
                        "product_id"),
                @Index(name = "idx_user_product_event_type", columnList =
                        "event_type"),
                @Index(name = "idx_user_product_event_created_at", columnList
                        = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProductEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private ProductEventType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
