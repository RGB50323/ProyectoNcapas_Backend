package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "size", nullable = false, length = 20)
    private String size;

    @Column(name = "color_name", nullable = false, length = 100)
    private String colorName;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "price_delta", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceDelta;
}