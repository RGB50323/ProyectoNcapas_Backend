package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.ErpExportStatus;
import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.common.mappers.ErpOrderMapper;
import com.uca.ecommerce.domain.dto.response.ErpBulkExportResponse;
import com.uca.ecommerce.domain.dto.response.ErpExportResponse;
import com.uca.ecommerce.domain.dto.response.ErpOrderResponse;
import com.uca.ecommerce.domain.entities.*;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ErpOrderRepository;
import com.uca.ecommerce.repository.OrderErpExportRepository;
import com.uca.ecommerce.repository.OrderItemRepository;
import com.uca.ecommerce.repository.OrderRepository;
import com.uca.ecommerce.services.ErpIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ErpIntegrationServiceImpl implements ErpIntegrationService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderErpExportRepository orderErpExportRepository;
    private final ErpOrderRepository erpOrderRepository;
    private final ErpOrderMapper erpOrderMapper;

    @Override
    @Transactional
    public ErpExportResponse exportOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        OrderErpExport export = orderErpExportRepository.findByOrderId(orderId)
                .orElseGet(() -> orderErpExportRepository.save(
                        OrderErpExport.builder()
                                .order(order)
                                .erpExportStatus(ErpExportStatus.PENDING_EXPORT)
                                .build()
                ));

        try {
            ErpOrder existingErpOrder = erpOrderRepository.findBySourceOrderId(orderId)
                    .orElse(null);
            if (existingErpOrder != null) {
                markExported(export, existingErpOrder.getErpReference());
                return buildExportResponse(export, existingErpOrder);
            }

            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            String validationMessage = validateForErp(order, items);
            if (validationMessage != null) {
                export.setErpExportStatus(ErpExportStatus.REJECTED);
                export.setErpReference(null);
                export.setErpExportedAt(null);
                export.setErpErrorMessage(validationMessage);
                orderErpExportRepository.save(export);
                return buildExportResponse(export, null);
            }

            ErpOrder erpOrder = buildErpOrder(order, items);
            ErpOrder savedErpOrder = erpOrderRepository.save(erpOrder);

            markExported(export, savedErpOrder.getErpReference());
            return buildExportResponse(export, savedErpOrder);
        } catch (Exception ex) {
            export.setErpExportStatus(ErpExportStatus.FAILED);
            export.setErpExportedAt(null);
            export.setErpErrorMessage(ex.getMessage());
            orderErpExportRepository.save(export);
            return buildExportResponse(export, null);
        }
    }

    @Override
    @Transactional
    public ErpBulkExportResponse exportPendingDeliveredOrders() {
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);
        List<ErpExportResponse> results = new ArrayList<>();
        int skippedCount = 0;

        for (Order order : deliveredOrders) {
            OrderErpExport existingExport = orderErpExportRepository.findByOrderId(order.getId())
                    .orElse(null);
            if (existingExport != null
                    && existingExport.getErpExportStatus() == ErpExportStatus.EXPORTED) {
                skippedCount++;
                continue;
            }
            results.add(exportOrder(order.getId()));
        }

        int exportedCount = (int) results.stream()
                .filter(result -> result.getErpExportStatus() == ErpExportStatus.EXPORTED)
                .count();
        int rejectedCount = (int) results.stream()
                .filter(result -> result.getErpExportStatus() == ErpExportStatus.REJECTED)
                .count();
        int failedCount = (int) results.stream()
                .filter(result -> result.getErpExportStatus() == ErpExportStatus.FAILED)
                .count();

        return ErpBulkExportResponse.builder()
                .exportedCount(exportedCount)
                .rejectedCount(rejectedCount)
                .failedCount(failedCount)
                .skippedCount(skippedCount)
                .processedCount(results.size())
                .results(results)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErpExportResponse> getAllExportStatuses() {
        return orderErpExportRepository.findAll().stream()
                .map(export -> {
                    ErpOrder erpOrder = export.getErpReference() == null
                            ? null
                            : erpOrderRepository.findByErpReference(export.getErpReference())
                                    .orElse(null);
                    return buildExportResponse(export, erpOrder);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErpOrderResponse> getAllErpOrders() {
        return erpOrderMapper.toDtoList(erpOrderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ErpOrderResponse getErpOrderByReference(String erpReference) {
        ErpOrder erpOrder = erpOrderRepository.findByErpReference(erpReference)
                .orElseThrow(() -> new NotFoundException("ERP order not found"));
        return erpOrderMapper.toDto(erpOrder);
    }

    private void markExported(OrderErpExport export, String erpReference) {
        export.setErpExportStatus(ErpExportStatus.EXPORTED);
        export.setErpReference(erpReference);
        export.setErpExportedAt(LocalDateTime.now());
        export.setErpErrorMessage(null);
        orderErpExportRepository.save(export);
    }

    private ErpExportResponse buildExportResponse(OrderErpExport export, ErpOrder erpOrder) {
        return ErpExportResponse.builder()
                .orderId(export.getOrder().getId())
                .erpExportStatus(export.getErpExportStatus())
                .erpReference(export.getErpReference())
                .erpExportedAt(export.getErpExportedAt())
                .erpErrorMessage(export.getErpErrorMessage())
                .erpOrder(erpOrder != null ? erpOrderMapper.toDto(erpOrder) : null)
                .build();
    }

    private ErpOrder buildErpOrder(Order order, List<OrderItem> items) {
        ErpOrder erpOrder = ErpOrder.builder()
                .erpReference(generateErpReference())
                .sourceOrderId(order.getId())
                .customerId(order.getCustomer().getUuid())
                .customerName(fullName(order.getCustomer()))
                .customerEmail(order.getCustomer().getEmail())
                .shippingAddress(formatAddress(order.getShippingAddress()))
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .discountAmount(order.getDiscountAmount())
                .total(order.getTotal())
                .orderDate(order.getCreatedAt())
                .receivedAt(LocalDateTime.now())
                .build();

        List<ErpOrderItem> erpItems = items.stream()
                .map(item -> buildErpOrderItem(erpOrder, item))
                .toList();
        erpOrder.getItems().addAll(erpItems);
        return erpOrder;
    }

    private ErpOrderItem buildErpOrderItem(ErpOrder erpOrder, OrderItem item) {
        ProductVariant variant = item.getVariant();
        return ErpOrderItem.builder()
                .erpOrder(erpOrder)
                .sourceOrderItemId(item.getId())
                .productId(item.getProduct().getId())
                .sku(item.getProduct().getSku())
                .productName(item.getProduct().getName())
                .variantId(variant != null ? variant.getId() : null)
                .variantDescription(variant != null ? formatVariant(variant) : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private String validateForErp(Order order, List<OrderItem> items) {
        List<String> errors = new ArrayList<>();

        if (order.getId() == null) errors.add("Order ID is required");
        if (order.getStatus() != OrderStatus.DELIVERED) {
            errors.add("Order must be DELIVERED before it can be exported to ERP");
        }
        if (order.getCustomer() == null) {
            errors.add("Customer information is required");
        } else {
            if (isBlank(fullName(order.getCustomer()))) errors.add("Customer name is required");
            if (isBlank(order.getCustomer().getEmail())) errors.add("Customer email is required");
        }
        if (order.getShippingAddress() == null) errors.add("Shipping address is required");
        if (order.getTotal() == null) errors.add("Total amount is required");
        if (order.getCreatedAt() == null) errors.add("Order date is required");

        if (items == null || items.isEmpty()) {
            errors.add("Order items are required");
        } else {
            for (OrderItem item : items) {
                validateItem(item, errors);
            }
        }

        return errors.isEmpty() ? null : String.join("; ", errors);
    }

    private void validateItem(OrderItem item, List<String> errors) {
        if (item.getId() == null) errors.add("Order item ID is required");
        if (item.getProduct() == null) {
            errors.add("Product is required");
        } else {
            if (isBlank(item.getProduct().getSku())) errors.add("Product SKU is required");
            if (isBlank(item.getProduct().getName())) errors.add("Product name is required");
        }
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            errors.add("Quantity must be greater than zero");
        }
        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Unit price is required and cannot be negative");
        }
        if (item.getTotalPrice() == null || item.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Item total amount is required and cannot be negative");
        }
    }

    private synchronized String generateErpReference() {
        long next = erpOrderRepository.count() + 1;
        String reference;
        do {
            reference = "ERP-" + Year.now().getValue() + "-" + String.format("%06d", next);
            next++;
        } while (erpOrderRepository.existsByErpReference(reference));
        return reference;
    }

    private String fullName(User user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    private String formatAddress(Address address) {
        List<String> parts = new ArrayList<>();
        addIfPresent(parts, address.getStreet());
        addIfPresent(parts, address.getCity());
        addIfPresent(parts, address.getState());
        addIfPresent(parts, address.getCountry());
        addIfPresent(parts, address.getPostalCode());
        return String.join(", ", parts);
    }

    private String formatVariant(ProductVariant variant) {
        return variant.getSize() + " / " + variant.getColorName();
    }

    private void addIfPresent(List<String> parts, String value) {
        if (!isBlank(value)) {
            parts.add(value);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
