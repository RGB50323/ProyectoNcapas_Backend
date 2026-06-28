package com.uca.ecommerce.domain.entities;

import com.uca.ecommerce.common.Enums.DropType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private SellerProfile owner;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "drop_date", nullable = false)
    private LocalDateTime dropDate;

    @Column(name = "units", nullable = false)
    private Integer units;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DropType type;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
