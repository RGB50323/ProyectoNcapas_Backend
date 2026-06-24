package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController extends BaseController {

    private final AnalyticsService analyticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cart-conversion")
    public ResponseEntity<GeneralResponse> getCartConversionReport() {
        return buildResponse(
                "Cart conversion report retrieved successfully",
                HttpStatus.OK,
                analyticsService.getCartConversionReport()
        );
    }
}