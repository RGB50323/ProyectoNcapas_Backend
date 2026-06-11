package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.common.mappers.PaymentMapper;
import com.uca.ecommerce.domain.dto.request.payment.CreatePaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.PatchPaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.UpdatePaymentRequest;
import com.uca.ecommerce.domain.dto.response.PaymentResponse;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.Payment;
import com.uca.ecommerce.exceptions.InvalidPaymentException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.OrderRepository;
import com.uca.ecommerce.repository.PaymentRepository;
import com.uca.ecommerce.services.PaymentService;
import com.uca.ecommerce.services.payment.PaymentContext;
import com.uca.ecommerce.services.payment.PaymentStrategy;
import com.uca.ecommerce.services.payment.PaymentStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentStrategyResolver strategyResolver;

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentMapper.toDtoList(paymentRepository.findAll());
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(UUID orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return paymentMapper.toDtoList(paymentRepository.findByOrderId(orderId));
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentMapper.toDtoList(paymentRepository.findByStatus(status));
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        return paymentMapper.toDto(findOrThrow(id));
    }

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Validar que BANK_TRANSFER tenga transactionId
        if (request.getMethod().name().equals("BANK_TRANSFER")
                && request.getTransactionId() == null)
            throw new InvalidPaymentException(
                    "Transaction ID is required for bank transfers"
            );

        // Resolver la estrategia y ejecutar el pago
        PaymentStrategy strategy = strategyResolver.resolve(request.getMethod());
        PaymentContext context = PaymentContext.builder()
                .orderId(order.getId())
                .amount(order.getTotal())
                .transactionId(request.getTransactionId())
                .build();

        PaymentStatus status = strategy.execute(context);

        return paymentMapper.toDto(
                paymentRepository.save(
                        paymentMapper.toEntityCreate(request, order, status)
                )
        );
    }

    @Override
    public PaymentResponse updatePayment(UpdatePaymentRequest request, UUID id) {
        Payment existing = findOrThrow(id);

        if (Boolean.TRUE.equals(request.getMethod().name().equals("BANK_TRANSFER"))
                && request.getTransactionId() == null)
            throw new InvalidPaymentException(
                    "Transaction ID is required for bank transfers"
            );

        return paymentMapper.toDto(
                paymentRepository.save(
                        paymentMapper.toEntityUpdate(request, existing)
                )
        );
    }

    @Override
    public PaymentResponse patchPayment(PatchPaymentRequest request, UUID id) {
        Payment existing = findOrThrow(id);

        if (Boolean.TRUE.equals(request.getRemoveTransactionId())
                && request.getTransactionId() != null)
            throw new InvalidPaymentException(
                    "Transaction ID cannot be provided when removeTransactionId is true"
            );

        return paymentMapper.toDto(
                paymentRepository.save(
                        paymentMapper.toEntityPatch(request, existing)
                )
        );
    }

    @Override
    public PaymentResponse deletePayment(UUID id) {
        Payment existing = findOrThrow(id);
        paymentRepository.deleteById(id);
        return paymentMapper.toDto(existing);
    }

    private Payment findOrThrow(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }
}