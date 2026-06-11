package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class DebitCardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.DEBIT_CARD;
    }

    @Override
    public PaymentStatus execute(PaymentContext context) {
        return PaymentStatus.COMPLETED;
    }
}