package com.uca.ecommerce.services.discount;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.exceptions.InvalidCouponException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Spring injects every DiscountStrategy bean, so a new type is picked up automatically
@Component
public class DiscountStrategyResolver {

    private final Map<DiscountType, DiscountStrategy> strategies;

    public DiscountStrategyResolver(List<DiscountStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(DiscountStrategy::getType, Function.identity()));
    }

    public DiscountStrategy resolve(DiscountType type) {
        DiscountStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new InvalidCouponException("Unsupported discount type: " + type);
        }
        return strategy;
    }

    public java.util.Collection<DiscountStrategy> getRegisteredStrategies() {
        return strategies.values();
    }
}
