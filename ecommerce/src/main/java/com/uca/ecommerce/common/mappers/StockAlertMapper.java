package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.stockAlert.CreateStockAlertRequest;
import com.uca.ecommerce.domain.dto.response.StockAlertResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.StockAlert;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockAlertMapper {

    public StockAlert toEntityCreate(CreateStockAlertRequest request, User user, Product product) {
        return StockAlert.builder()
                .user(user)
                .product(product)
                .build();
    }

    public StockAlertResponse toDto(StockAlert alert) {
        return StockAlertResponse.builder()
                .id(alert.getId())
                .userId(alert.getUser().getUuid())
                .userFullName(alert.getUser().getFirstName() + " " + alert.getUser().getLastName())
                .productId(alert.getProduct().getId())
                .productName(alert.getProduct().getName())
                .notifiedAt(alert.getNotifiedAt())
                .build();
    }

    public List<StockAlertResponse> toDtoList(List<StockAlert> alerts) {
        return alerts.stream()
                .map(this::toDto)
                .toList();
    }
}