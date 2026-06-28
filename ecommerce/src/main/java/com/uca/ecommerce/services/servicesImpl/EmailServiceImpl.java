package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final RestClient resendClient;
    private final String from;

    public EmailServiceImpl(
            @Value("${app.resend.api-key}") String apiKey,
            @Value("${app.mail.from}") String from) {
        this.from = from;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(15));
        this.resendClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String html = """
                <p>You requested a password reset.</p>
                <p>Your password reset code is: <strong>%s</strong></p>
                <p>Enter this code on the password reset page along with your new password.</p>
                <p>This code expires in 15 minutes.</p>
                <p>If you did not request this, please ignore this email.</p>
                """.formatted(token);
        send(Map.of(
                "from", from,
                "to", List.of(to),
                "subject", "Password Reset Request",
                "html", html
        ));
    }

    @Override
    @Async
    public void sendInvoiceEmail(String to, String customerName, String controlNumber, byte[] xml, byte[] pdf) {
        send(Map.of(
                "from", from,
                "to", List.of(to),
                "subject", "Gracias por tu compra en K LAB - Factura " + controlNumber,
                "html", buildInvoiceHtml(firstName(customerName), controlNumber),
                "attachments", List.of(
                        attachment("factura-" + controlNumber + ".xml", xml),
                        attachment("factura-" + controlNumber + ".pdf", pdf)
                )
        ));
    }

    private Map<String, String> attachment(String filename, byte[] content) {
        return Map.of(
                "filename", filename,
                "content", Base64.getEncoder().encodeToString(content)
        );
    }

    private void send(Map<String, Object> body) {
        try {
            resendClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.error("Email sending failed for {}", body.get("to"), ex);
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
