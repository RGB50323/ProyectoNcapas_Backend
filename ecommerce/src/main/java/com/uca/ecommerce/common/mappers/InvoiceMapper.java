package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.response.InvoiceResponse;
import com.uca.ecommerce.domain.entities.Invoice;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {

    public Invoice toEntity(Order order) {
        User customer = order.getCustomer();
        String fullName = ((customer.getFirstName() != null ? customer.getFirstName() : "")
                + " "
                + (customer.getLastName() != null ? customer.getLastName() : "")).trim();

        return Invoice.builder()
                .order(order)
                .customerName(fullName.isEmpty() ? customer.getEmail() : fullName)
                .customerEmail(customer.getEmail())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .discountAmount(order.getDiscountAmount())
                .total(order.getTotal())
                .build();
    }

    public InvoiceResponse toDto(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .orderId(invoice.getOrder().getId())
                .controlNumber(invoice.getControlNumber())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .subtotal(invoice.getSubtotal())
                .shippingCost(invoice.getShippingCost())
                .discountAmount(invoice.getDiscountAmount())
                .total(invoice.getTotal())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .build();
    }

    public List<InvoiceResponse> toDtoList(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::toDto)
                .toList();
    }
}
