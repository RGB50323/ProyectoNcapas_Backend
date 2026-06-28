package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.response.ErpBulkExportResponse;
import com.uca.ecommerce.domain.dto.response.ErpExportResponse;
import com.uca.ecommerce.domain.dto.response.ErpOrderResponse;

import java.util.List;
import java.util.UUID;

public interface ErpIntegrationService {
    ErpExportResponse exportOrder(UUID orderId);
    ErpBulkExportResponse exportPendingDeliveredOrders();
    List<ErpExportResponse> getAllExportStatuses();
    List<ErpOrderResponse> getAllErpOrders();
    ErpOrderResponse getErpOrderByReference(String erpReference);
}
