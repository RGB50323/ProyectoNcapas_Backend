package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.domain.entities.Coupon;

import java.math.BigDecimal;

// Adding a new discount type = new implementation of this interface, no changes to existing logic (Open/Closed)
public interface DiscountStrategy {

    DiscountType getType();

    BigDecimal calculate(DiscountContext context, Coupon coupon);

    // Human label exposed to the UI; defaults to the enum name so a new strategy needs no extra wiring
    default String getLabel() {
        return getType().name();
    }

    // Whether this discount uses the coupon's "value" field (percentage/amount). The UI hides it otherwise.
    default boolean usesValue() {
        return true;
    }
}
