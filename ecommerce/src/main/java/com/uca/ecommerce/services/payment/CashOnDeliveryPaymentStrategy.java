package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CASH_ON_DELIVERY;
    }

    @Override
    // Cash on delivery is always pending until delivered
    public PaymentStatus execute(PaymentContext context) {
        return PaymentStatus.PENDING;
    }
}