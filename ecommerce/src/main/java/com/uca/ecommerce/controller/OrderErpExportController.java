package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ErpIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderErpExportController extends BaseController {

    private final ErpIntegrationService erpIntegrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/export-pending-to-erp")
    public ResponseEntity<GeneralResponse> exportPendingOrdersToErp() {
        return buildResponse(
                "Pending delivered orders export to ERP processed successfully",
                HttpStatus.OK,
                erpIntegrationService.exportPendingDeliveredOrders()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/export-to-erp")
    public ResponseEntity<GeneralResponse> exportOrderToErp(@PathVariable UUID id) {
        return buildResponse(
                "Order export to ERP processed successfully",
                HttpStatus.OK,
                erpIntegrationService.exportOrder(id)
        );
    }
}
