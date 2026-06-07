package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shipping_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(name = "eta", nullable = false, length = 50)
    private String eta;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
