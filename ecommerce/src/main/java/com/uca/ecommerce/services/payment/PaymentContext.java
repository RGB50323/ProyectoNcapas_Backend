package com.uca.ecommerce.services.payment;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

// Carries everything a payment strategy may need
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentContext {
    private UUID orderId;
    private BigDecimal amount;
    private String transactionId; // opcional según el metodo
}