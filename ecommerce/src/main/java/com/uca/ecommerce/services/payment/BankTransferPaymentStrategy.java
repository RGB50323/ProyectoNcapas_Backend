package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class BankTransferPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.BANK_TRANSFER;
    }

    @Override
    // Bank transfers start as PENDING until confirmed
    public PaymentStatus execute(PaymentContext context) {
        return PaymentStatus.PENDING;
    }
}