package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText(
                "You requested a password reset.\n\n" +
                        "Click the link below to reset your password:\n" +
                        frontendUrl + "/reset-password?token=" + token + "\n\n" +
                        "This link expires in 15 minutes.\n\n" +
                        "If you did not request this, please ignore this email."
        );
        mailSender.send(message);
    }
}