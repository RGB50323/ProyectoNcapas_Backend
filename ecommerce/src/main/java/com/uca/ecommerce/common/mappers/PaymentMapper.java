package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.domain.dto.request.payment.CreatePaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.PatchPaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.UpdatePaymentRequest;
import com.uca.ecommerce.domain.dto.response.PaymentResponse;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentMapper {

    public Payment toEntityCreate(
            CreatePaymentRequest request,
            Order order,
            PaymentStatus status
    ) {
        return Payment.builder()
                .order(order)
                .method(request.getMethod())
                .status(status)
                .amount(order.getTotal())
                .transactionId(request.getTransactionId())
                .paidAt(status == PaymentStatus.COMPLETED ? LocalDateTime.now() : null)
                .build();
    }

    public Payment toEntityUpdate(
            UpdatePaymentRequest request,
            Payment existing
    ) {
        return Payment.builder()
                .id(existing.getId())
                .order(existing.getOrder())
                .method(request.getMethod())
                .status(request.getStatus())
                .amount(existing.getAmount())
                .transactionId(request.getTransactionId())
                .paidAt(request.getStatus() == PaymentStatus.COMPLETED
                        && existing.getPaidAt() == null
                        ? LocalDateTime.now()
                        : existing.getPaidAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public Payment toEntityPatch(
            PatchPaymentRequest request,
            Payment existing
    ) {
        PaymentStatus newStatus = request.getStatus() != null
                ? request.getStatus() : existing.getStatus();

        return Payment.builder()
                .id(existing.getId())
                .order(existing.getOrder())
                .method(existing.getMethod())
                .status(newStatus)
                .amount(existing.getAmount())
                .transactionId(Boolean.TRUE.equals(request.getRemoveTransactionId())
                        ? null
                        : request.getTransactionId() != null
                          ? request.getTransactionId()
                          : existing.getTransactionId())
                .paidAt(newStatus == PaymentStatus.COMPLETED
                        && existing.getPaidAt() == null
                        ? LocalDateTime.now()
                        : existing.getPaidAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public PaymentResponse toDto(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderTotal(payment.getOrder().getTotal())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public List<PaymentResponse> toDtoList(List<Payment> payments) {
        return payments.stream()
                .map(this::toDto)
                .toList();
    }
}