package com.uca.ecommerce.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErpOrderResponse {
    private UUID id;
    private String erpReference;
    private UUID sourceOrderId;
    private UUID customerId;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private LocalDateTime receivedAt;
    private List<ErpOrderItemResponse> items;
}
