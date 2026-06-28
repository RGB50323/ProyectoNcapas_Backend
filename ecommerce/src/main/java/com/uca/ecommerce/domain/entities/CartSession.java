package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.CartSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cart_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartSessionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime convertedAt;

    @Column
    private LocalDateTime abandonedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean abandonedManually = false;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}