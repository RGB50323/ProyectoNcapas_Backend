package com.uca.ecommerce.domain.dto.request.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    // Opcional — requerido para BANK_TRANSFER
    private String transactionId;
}