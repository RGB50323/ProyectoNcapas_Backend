package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.ShipmentMapper;
import com.uca.ecommerce.domain.dto.response.ShipmentResponse;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.OrderRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final OrderRepository orderRepository;
    private final ShipmentMapper shipmentMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ShipmentResponse getByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        validateAccess(order);
        return shipmentMapper.toDto(order, trackingNumber(order), estimatedDelivery(order));
    }

    private String trackingNumber(Order order) {
        String raw = order.getId().toString().replace("-", "").toUpperCase();
        return "TRK-" + raw.substring(0, 10);
    }

    private LocalDate estimatedDelivery(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            return null;
        }
        String eta = order.getShippingMethod() != null ? order.getShippingMethod().getEta() : null;
        return order.getCreatedAt().toLocalDate().plusDays(parseEtaDays(eta));
    }

    private long parseEtaDays(String eta) {
        if (eta == null) return 5;
        Matcher matcher = Pattern.compile("\\d+").matcher(eta);
        long days = 0;
        boolean found = false;
        while (matcher.find()) {
            found = true;
            days = Math.max(days, Long.parseLong(matcher.group()));
        }
        if (!found) return 5;
        String lower = eta.toLowerCase();
        if (lower.contains("semana") || lower.contains("week")) return days * 7;
        if (lower.contains("hora") || lower.contains("hour")) return 1;
        return days;
    }

    private void validateAccess(Order order) {
        User current = currentUserProvider.getCurrentUser();
        if (current.getRole() == Role.ADMIN || current.getRole() == Role.SELLER) {
            return;
        }
        if (!order.getCustomer().getUuid().equals(current.getUuid())) {
            throw new AccessDeniedException("You do not have permission to access this shipment");
        }
    }
}
