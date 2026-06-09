package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.stockAlert.CreateStockAlertRequest;
import com.uca.ecommerce.domain.dto.response.StockAlertResponse;

import java.util.List;
import java.util.UUID;

public interface StockAlertService {
    List<StockAlertResponse> getAllAlerts();
    List<StockAlertResponse> getAlertsByUserId(UUID userId);
    List<StockAlertResponse> getAlertsByProductId(UUID productId);
    StockAlertResponse getAlertById(UUID id);
    StockAlertResponse createAlert(CreateStockAlertRequest request);
    StockAlertResponse deleteAlert(UUID id);
}