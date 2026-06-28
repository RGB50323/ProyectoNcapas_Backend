package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.domain.entities.Coupon;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.PERCENTAGE;
    }

    @Override
    public String getLabel() {
        return "Porcentaje";
    }

    @Override
    public BigDecimal calculate(DiscountContext context, Coupon coupon) {
        BigDecimal discount = context.getSubtotal()
                .multiply(coupon.getValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return discount.min(context.getSubtotal());
    }
}
