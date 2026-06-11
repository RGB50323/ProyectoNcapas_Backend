package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public PaymentStatus execute(PaymentContext context) {
        // Aquí iría la integración con pasarela de pago real
        // Por ahora simulamos que siempre es exitoso
        return PaymentStatus.COMPLETED;
    }
}