package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.response.InvoiceResponse;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse issueAndSend(UUID orderId);

    InvoiceResponse resendForOrder(UUID orderId);

    InvoiceResponse getByOrderId(UUID orderId);

    byte[] getPdfByOrderId(UUID orderId);

    byte[] getXmlByOrderId(UUID orderId);

    List<InvoiceResponse> getAllInvoices();
}
