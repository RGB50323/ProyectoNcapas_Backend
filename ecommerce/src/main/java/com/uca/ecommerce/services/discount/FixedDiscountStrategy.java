package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.domain.entities.Coupon;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.FIXED;
    }

    @Override
    public BigDecimal calculate(DiscountContext context, Coupon coupon) {
        return coupon.getValue().min(context.getSubtotal());
    }
}
