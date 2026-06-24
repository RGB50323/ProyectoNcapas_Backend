package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.InvoiceStatus;
import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.InvoiceMapper;
import com.uca.ecommerce.domain.dto.response.InvoiceResponse;
import com.uca.ecommerce.domain.entities.Invoice;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.domain.entities.OrderItem;
import com.uca.ecommerce.domain.entities.Payment;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.InvoiceRepository;
import com.uca.ecommerce.repository.OrderItemRepository;
import com.uca.ecommerce.repository.OrderRepository;
import com.uca.ecommerce.repository.PaymentRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.InvoiceService;
import com.uca.ecommerce.services.EmailService;
import com.uca.ecommerce.services.invoice.PdfInvoiceGenerator;
import com.uca.ecommerce.services.invoice.XmlInvoiceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final XmlInvoiceGenerator xmlInvoiceGenerator;
    private final PdfInvoiceGenerator pdfInvoiceGenerator;
    private final EmailService emailService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public InvoiceResponse issueAndSend(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        return invoiceMapper.toDto(generateAndSend(order));
    }

    @Override
    public InvoiceResponse resendForOrder(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        validateAccess(order);
        return invoiceMapper.toDto(generateAndSend(order));
    }

    @Override
    public InvoiceResponse getByOrderId(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        validateAccess(order);
        return invoiceMapper.toDto(getOrCreate(order));
    }

    @Override
    public byte[] getPdfByOrderId(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        validateAccess(order);
        Invoice invoice = getOrCreate(order);
        return pdfInvoiceGenerator.generate(invoice, order, orderItemRepository.findByOrderId(orderId), paymentMethodFor(orderId));
    }

    @Override
    public byte[] getXmlByOrderId(UUID orderId) {
        Order order = findOrderOrThrow(orderId);
        validateAccess(order);
        Invoice invoice = getOrCreate(order);
        return xmlInvoiceGenerator.generate(invoice, order, orderItemRepository.findByOrderId(orderId), paymentMethodFor(orderId));
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceMapper.toDtoList(invoiceRepository.findAll());
    }

    private Invoice generateAndSend(Order order) {
        Invoice invoice = getOrCreate(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        String paymentMethod = paymentMethodFor(order.getId());
        byte[] xml = xmlInvoiceGenerator.generate(invoice, order, items, paymentMethod);
        byte[] pdf = pdfInvoiceGenerator.generate(invoice, order, items, paymentMethod);
        emailService.sendInvoiceEmail(invoice.getCustomerEmail(), invoice.getCustomerName(), invoice.getControlNumber(), xml, pdf);
        invoice.setStatus(InvoiceStatus.SENT);
        return invoiceRepository.save(invoice);
    }

    private String paymentMethodFor(UUID orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .findFirst()
                .or(() -> payments.stream().findFirst())
                .map(p -> p.getMethod().name())
                .orElse(null);
    }

    private Invoice getOrCreate(Order order) {
        return invoiceRepository.findByOrderId(order.getId())
                .orElseGet(() -> invoiceRepository.save(invoiceMapper.toEntity(order)));
    }

    private Order findOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private void validateAccess(Order order) {
        User current = currentUserProvider.getCurrentUser();
        if (current.getRole() == Role.ADMIN || current.getRole() == Role.SELLER) {
            return;
        }
        if (!order.getCustomer().getUuid().equals(current.getUuid())) {
            throw new AccessDeniedException("You do not have permission to access this invoice");
        }
    }
}
