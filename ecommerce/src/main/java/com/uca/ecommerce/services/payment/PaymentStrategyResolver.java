package com.uca.ecommerce.services.payment;

import com.uca.ecommerce.common.Enums.PaymentMethod;
import com.uca.ecommerce.exceptions.InvalidPaymentException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyResolver {

    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentStrategyResolver(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getMethod, Function.identity()));
    }

    public PaymentStrategy resolve(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new InvalidPaymentException("Unsupported payment method: " + method);
        }
        return strategy;
    }
}