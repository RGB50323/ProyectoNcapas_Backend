package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "erp_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErpOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "erp_order_id", nullable = false)
    private ErpOrder erpOrder;

    @Column(name = "source_order_item_id", nullable = false)
    private UUID sourceOrderItemId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "variant_description", length = 255)
    private String variantDescription;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
}
