package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "drop_products",
        uniqueConstraints = @UniqueConstraint(columnNames = {"drop_id", "product_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DropProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "drop_id", nullable = false)
    private Drop drop;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
