package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.domain.entities.Coupon;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TwoForOneDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.TWO_FOR_ONE;
    }

    @Override
    public String getLabel() {
        return "2x1";
    }

    @Override
    public boolean usesValue() {
        return false;
    }

    // For every pair of equal units the cheapest one is free, applied per cart line
    @Override
    public BigDecimal calculate(DiscountContext context, Coupon coupon) {
        BigDecimal discount = BigDecimal.ZERO;
        if (context.getItems() == null) return discount;

        for (DiscountContext.LineItem item : context.getItems()) {
            int freeUnits = item.getQuantity() / 2;
            discount = discount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(freeUnits)));
        }
        return discount;
    }
}
