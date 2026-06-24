package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController extends BaseController {

    private final InvoiceService invoiceService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllInvoices() {
        return buildResponse(
                "Invoices retrieved successfully",
                HttpStatus.OK,
                invoiceService.getAllInvoices()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','BUYER')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> getInvoiceByOrder(@PathVariable UUID orderId) {
        return buildResponse(
                "Invoice retrieved successfully",
                HttpStatus.OK,
                invoiceService.getByOrderId(orderId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','BUYER')")
    @PostMapping("/order/{orderId}/email")
    public ResponseEntity<GeneralResponse> sendInvoiceByOrder(@PathVariable UUID orderId) {
        return buildResponse(
                "Invoice sent successfully",
                HttpStatus.OK,
                invoiceService.resendForOrder(orderId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','BUYER')")
    @GetMapping("/order/{orderId}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable UUID orderId) {
        byte[] pdf = invoiceService.getPdfByOrderId(orderId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + orderId + ".pdf\"")
                .body(pdf);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','BUYER')")
    @GetMapping("/order/{orderId}/xml")
    public ResponseEntity<byte[]> getInvoiceXml(@PathVariable UUID orderId) {
        byte[] xml = invoiceService.getXmlByOrderId(orderId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + orderId + ".xml\"")
                .body(xml);
    }
}
