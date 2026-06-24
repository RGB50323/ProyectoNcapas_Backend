package com.uca.ecommerce.services;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);

    void sendInvoiceEmail(String to, String customerName, String controlNumber, byte[] xml, byte[] pdf);
}