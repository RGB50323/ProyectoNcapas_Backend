package com.uca.ecommerce.domain.dto.request.payment;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchPaymentRequest {

    private PaymentStatus status;
    private String transactionId;
    private Boolean removeTransactionId;
}