package com.uca.ecommerce.services;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}