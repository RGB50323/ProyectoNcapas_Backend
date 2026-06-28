package com.uca.ecommerce.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "erp_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErpOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "erp_reference", nullable = false, unique = true, length = 50)
    private String erpReference;

    @Column(name = "source_order_id", nullable = false, unique = true)
    private UUID sourceOrderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @OneToMany(mappedBy = "erpOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ErpOrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (receivedAt == null) receivedAt = LocalDateTime.now();
    }
}
