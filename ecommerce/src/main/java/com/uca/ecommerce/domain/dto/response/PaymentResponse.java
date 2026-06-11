package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal orderTotal;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}