package com.uca.ecommerce.services;

import com.uca.ecommerce.common.Enums.CartSessionStatus;
import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.repository.CartSessionRepository;
import com.uca.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final CartSessionRepository cartSessionRepository;
    private final OrderRepository orderRepository;

    public Map<String, Object> getCartConversionReport() {

        long totalSessions = cartSessionRepository.count();
        long convertedSessions = cartSessionRepository.countByStatus(CartSessionStatus.CONVERTED);
        long abandonedSessions = cartSessionRepository.countByStatus(CartSessionStatus.ABANDONED);
        long activeSessions = cartSessionRepository.countByStatus(CartSessionStatus.ACTIVE);

        double conversionRate = totalSessions == 0 ? 0 :
                Math.round((convertedSessions * 100.0 / totalSessions) * 10.0) / 10.0;

        double abandonRate = totalSessions == 0 ? 0 :
                Math.round((abandonedSessions * 100.0 / totalSessions) * 10.0) / 10.0;

        List<Order> allOrders = orderRepository.findAll();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long deliveredCount = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();

        BigDecimal averageOrderValue = deliveredCount == 0 ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(deliveredCount), 2, RoundingMode.HALF_UP);

        Map<String, Long> ordersByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalSessions", totalSessions);
        report.put("activeSessions", activeSessions);
        report.put("convertedSessions", convertedSessions);
        report.put("abandonedSessions", abandonedSessions);
        report.put("conversionRate", conversionRate);
        report.put("abandonRate", abandonRate);
        report.put("totalRevenue", totalRevenue);
        report.put("averageOrderValue", averageOrderValue);
        report.put("ordersByStatus", ordersByStatus);

        return report;
    }
}