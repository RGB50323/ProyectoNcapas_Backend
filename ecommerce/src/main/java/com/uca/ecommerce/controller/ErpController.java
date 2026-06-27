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
@RequestMapping("/api/erp")
@RequiredArgsConstructor
public class ErpController extends BaseController {

    private final ErpIntegrationService erpIntegrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exports")
    public ResponseEntity<GeneralResponse> getAllExportStatuses() {
        return buildResponse(
                "ERP export statuses retrieved successfully",
                HttpStatus.OK,
                erpIntegrationService.getAllExportStatuses()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<GeneralResponse> getAllErpOrders() {
        return buildResponse(
                "ERP orders retrieved successfully",
                HttpStatus.OK,
                erpIntegrationService.getAllErpOrders()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders/{erpReference}")
    public ResponseEntity<GeneralResponse> getErpOrderByReference(
            @PathVariable String erpReference
    ) {
        return buildResponse(
                "ERP order retrieved successfully",
                HttpStatus.OK,
                erpIntegrationService.getErpOrderByReference(erpReference)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/orders/{orderId}/export")
    public ResponseEntity<GeneralResponse> exportOrderToErp(
            @PathVariable UUID orderId
    ) {
        return buildResponse(
                "Order exported to ERP successfully",
                HttpStatus.OK,
                erpIntegrationService.exportOrder(orderId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/orders/export-pending")
    public ResponseEntity<GeneralResponse> exportPendingOrdersToErp() {
        return buildResponse(
                "Pending orders exported to ERP successfully",
                HttpStatus.OK,
                erpIntegrationService.exportPendingDeliveredOrders()
        );
    }
}