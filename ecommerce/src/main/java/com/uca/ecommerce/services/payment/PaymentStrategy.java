package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.common.Enums.PaymentStatus;

// Adding a new payment method = new implementation, no changes to existing logic (Open/Closed)
public interface PaymentStrategy {
    PaymentMethod getMethod();
    PaymentStatus execute(PaymentContext context);
}