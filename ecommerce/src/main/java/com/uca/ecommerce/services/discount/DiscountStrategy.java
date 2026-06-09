package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.domain.entities.Coupon;

import java.math.BigDecimal;

// Adding a new discount type = new implementation of this interface, no changes to existing logic (Open/Closed)
public interface DiscountStrategy {

    DiscountType getType();

    BigDecimal calculate(DiscountContext context, Coupon coupon);
}
