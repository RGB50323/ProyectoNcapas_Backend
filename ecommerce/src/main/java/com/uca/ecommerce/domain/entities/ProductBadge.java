package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "product_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "label", nullable = false, length = 50)
    private String label;
}