package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private UUID id;
    private UUID orderId;
    private String controlNumber;
    private String customerName;
    private String customerEmail;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;
}
