package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
public class ShipmentController extends BaseController {

    private final ShipmentService shipmentService;

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','BUYER')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> getShipmentByOrder(@PathVariable UUID orderId) {
        return buildResponse(
                "Shipment retrieved successfully",
                HttpStatus.OK,
                shipmentService.getByOrderId(orderId)
        );
    }
}
