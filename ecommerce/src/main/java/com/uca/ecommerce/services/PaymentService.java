package com.uca.ecommerce.services;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.domain.dto.request.payment.CreatePaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.PatchPaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.UpdatePaymentRequest;
import com.uca.ecommerce.domain.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<PaymentResponse> getAllPayments();
    List<PaymentResponse> getPaymentsByOrderId(UUID orderId);
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
    PaymentResponse getPaymentById(UUID id);
    PaymentResponse createPayment(CreatePaymentRequest request);
    PaymentResponse updatePayment(UpdatePaymentRequest request, UUID id);
    PaymentResponse patchPayment(PatchPaymentRequest request, UUID id);
    PaymentResponse deletePayment(UUID id);
}