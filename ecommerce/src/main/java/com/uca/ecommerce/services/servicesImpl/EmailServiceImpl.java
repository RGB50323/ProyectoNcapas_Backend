package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.exceptions.InvoiceGenerationException;
import com.uca.ecommerce.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
                        "Your password reset code is: " + token + "\n\n" +
                        "Enter this code on the password reset page along with your new password.\n\n" +
                        "This code expires in 15 minutes.\n\n" +
                        "If you did not request this, please ignore this email."
        );
        mailSender.send(message);
    }

    @Override
    public void sendInvoiceEmail(String to, String customerName, String controlNumber, byte[] xml, byte[] pdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Gracias por tu compra en K LAB - Factura " + controlNumber);
            helper.setText(buildInvoiceHtml(firstName(customerName), controlNumber), true);
            helper.addAttachment("factura-" + controlNumber + ".xml",
                    new ByteArrayResource(xml), "application/xml");
            helper.addAttachment("factura-" + controlNumber + ".pdf",
                    new ByteArrayResource(pdf), "application/pdf");
            mailSender.send(message);
        } catch (Exception ex) {
            throw new InvoiceGenerationException("Could not send invoice email");
        }
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.trim().split("\\s+")[0];
    }

    private String buildInvoiceHtml(String name, String controlNumber) {
        String greeting = name.isBlank() ? "Gracias por tu compra." : "Gracias por tu compra, " + name + ".";
        return """
                <div style="background:#1C1A16;padding:36px 0;font-family:Arial,Helvetica,sans-serif;">
                  <table align="center" width="520" cellpadding="0" cellspacing="0" style="width:520px;max-width:92%%;background:#211E18;border:1px solid #3A352C;">
                    <tr><td style="padding:32px 30px;">
                      <div style="color:#8A8174;font-size:11px;letter-spacing:4px;">K&middot;LAB &middot; MISTER K</div>
                      <h1 style="color:#ECE6DA;font-size:22px;letter-spacing:1px;font-weight:bold;margin:18px 0 10px;text-transform:uppercase;">%s</h1>
                      <p style="color:#B8AE9E;font-size:14px;line-height:1.7;margin:0 0 22px;">Tu pieza paso el protocolo de autenticacion de Mister K y ya esta en camino. Aqui tienes el comprobante de tu pedido.</p>
                      <div style="border:1px solid #3A352C;padding:16px 18px;margin:0 0 22px;">
                        <div style="color:#8A8174;font-size:10px;letter-spacing:2px;">NUMERO DE CONTROL</div>
                        <div style="color:#9C8163;font-size:16px;font-weight:bold;margin-top:6px;">%s</div>
                      </div>
                      <p style="color:#B8AE9E;font-size:13px;line-height:1.7;margin:0 0 26px;">Adjuntamos tu factura electronica en formato XML y PDF.</p>
                      <div style="border-top:1px solid #3A352C;padding-top:18px;">
                        <div style="color:#ECE6DA;font-size:12px;letter-spacing:3px;font-weight:bold;">CRUDO &middot; CERTERO &middot; COLECCIONABLE</div>
                        <div style="color:#8A8174;font-size:11px;line-height:1.6;margin-top:8px;">Cada pieza pasa el protocolo de Mister K. Si falla, no se envia.</div>
                      </div>
                      <div style="color:#5C564B;font-size:10px;letter-spacing:1px;margin-top:24px;">&copy; 2026 K LAB BY MISTER K. TODOS LOS DERECHOS RESERVADOS.</div>
                    </td></tr>
                  </table>
                </div>
                """.formatted(greeting, controlNumber);
    }
}