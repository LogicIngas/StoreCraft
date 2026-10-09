package com.example.loginpage.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * B1 — Brevo Email Service
 *
 * Sends transactional emails using the Brevo HTTP API as the primary transport.
 * Falls back to SMTP (via JavaMailSender) if:
 *   - the API key is not configured, OR
 *   - the HTTP API call fails
 *
 * Toggle between HTTP-only and SMTP-only via:
 *   brevo.use.api=true  (default — uses HTTP API, falls back to SMTP)
 *   brevo.use.api=false (forces SMTP directly)
 */
@Service
public class BrevoEmailService {

    @Value("${brevo.api.url:https://api.brevo.com/v3/smtp/email}")
    private String apiUrl;

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${app.mail.from:}")
    private String fromEmail;

    @Value("${app.mail.from-name:StoreCraft}")
    private String fromName;

    // Optional SMTP sender — only injected when BREVO_SMTP_HOST is configured
    private final JavaMailSender smtpSender;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * @param smtpSender nullable — Spring will inject null when the bean is absent
     *                   (e.g. in tests without SMTP configured)
     */
    public BrevoEmailService(
            @Nullable @Qualifier("brevoMailSender") JavaMailSender smtpSender) {
        this.smtpSender = smtpSender;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    public void sendPasswordReset(String to, String toName, String resetLink) {
        String subject = "Reset your StoreCraft password";
        String html = buildPasswordResetHtml(toName, resetLink);
        send(to, toName, subject, html);
    }

    public void sendEmailVerification(String to, String toName, String verifyLink) {
        String subject = "Verify your StoreCraft email address";
        String html = buildVerificationHtml(toName, verifyLink);
        send(to, toName, subject, html);
    }

    public void sendOrderConfirmationBuyer(String to, String toName,
                                           String orderId, String total, String date) {
        String subject = "Order Confirmed — #" + orderId;
        String html = buildOrderConfirmationHtml(toName, orderId, total, date);
        send(to, toName, subject, html);
    }

    public void sendNewOrderNotificationSeller(String to, String sellerName,
                                               String orderId, String buyerName,
                                               String total) {
        String subject = "New order received — #" + orderId;
        String html = buildNewOrderSellerHtml(sellerName, orderId, buyerName, total);
        send(to, sellerName, subject, html);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core dispatch logic
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Attempts HTTP API first; falls back to SMTP on any failure.
     * If neither transport is available the exception is swallowed and logged —
     * email is a best-effort side effect, not a blocking operation.
     */
    public void send(String to, String toName, String subject, String htmlContent) {
        boolean sent = false;

        if (apiKey != null && !apiKey.isBlank() && fromEmail != null && !fromEmail.isBlank()) {
            try {
                sendViaApi(to, toName, subject, htmlContent);
                sent = true;
                System.out.println("📧 [Brevo API] Email sent to: " + to);
            } catch (Exception e) {
                System.err.println("⚠️ Brevo HTTP API failed (" + e.getMessage() + "), falling back to SMTP");
            }
        }

        if (!sent && smtpSender != null) {
            try {
                sendViaSmtp(to, toName, subject, htmlContent);
                System.out.println("📧 [SMTP] Email sent to: " + to);
            } catch (Exception e) {
                System.err.println("❌ SMTP fallback also failed: " + e.getMessage());
            }
        } else if (!sent) {
            System.err.println("⚠️ Email NOT sent to " + to + " — no transport configured.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Transport implementations
    // ─────────────────────────────────────────────────────────────────────────

    private void sendViaApi(String to, String toName, String subject, String html)
            throws IOException, InterruptedException {
        // Escape quotes in name fields to avoid breaking the JSON
        String safeName    = toName   == null ? "" : toName.replace("\"", "\\\"");
        String safeFrom    = fromName == null ? "" : fromName.replace("\"", "\\\"");
        String safeSubject = subject  == null ? "" : subject.replace("\"", "\\\"");

        // Simple manual JSON construction — avoids pulling in Jackson just for this
        String body = "{"
                + "\"sender\":{\"email\":\"" + fromEmail + "\",\"name\":\"" + safeFrom + "\"},"
                + "\"to\":[{\"email\":\"" + to + "\",\"name\":\"" + safeName + "\"}],"
                + "\"subject\":\"" + safeSubject + "\","
                + "\"htmlContent\":" + jsonStringLiteral(html)
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Brevo API returned " + response.statusCode() + ": " + response.body());
        }
    }

    private void sendViaSmtp(String to, String toName, String subject, String html) throws Exception {
        jakarta.mail.internet.MimeMessage message = smtpSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        if (fromEmail != null && !fromEmail.isBlank()) {
            helper.setFrom(fromEmail, fromName != null ? fromName : "StoreCraft");
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        smtpSender.send(message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML template builders (inline CSS, mobile-responsive)
    // ─────────────────────────────────────────────────────────────────────────

    private String buildPasswordResetHtml(String name, String resetLink) {
        return emailWrapper("Password Reset",
                "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Hi <strong>" + esc(name) + "</strong>,</p>"
                + "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>We received a request to reset your StoreCraft password. "
                + "Click the button below — this link expires in <strong>1 hour</strong>.</p>"
                + "<div style='text-align:center;margin:32px 0'>"
                + "<a href='" + resetLink + "' style='display:inline-block;background:linear-gradient(135deg,#00897b,#004d40);color:#fff;text-decoration:none;font-size:16px;font-weight:600;padding:14px 36px;border-radius:8px'>Reset My Password</a>"
                + "</div>"
                + "<hr style='border:none;border-top:1px solid #eee;margin:24px 0'/>"
                + "<p style='color:#444;font-size:14px'>If you didn't request this, ignore this email — your password will not change.</p>"
                + "<p style='font-size:11px;color:#888;word-break:break-all'>Or copy: " + esc(resetLink) + "</p>");
    }

    private String buildVerificationHtml(String name, String verifyLink) {
        return emailWrapper("Verify Your Email",
                "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Hi <strong>" + esc(name) + "</strong>,</p>"
                + "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Welcome to StoreCraft! Please verify your email address to unlock all features.</p>"
                + "<div style='text-align:center;margin:32px 0'>"
                + "<a href='" + verifyLink + "' style='display:inline-block;background:linear-gradient(135deg,#00897b,#004d40);color:#fff;text-decoration:none;font-size:16px;font-weight:600;padding:14px 36px;border-radius:8px'>Verify My Email</a>"
                + "</div>"
                + "<p style='color:#888;font-size:13px'>This link expires in <strong>24 hours</strong>.</p>"
                + "<p style='font-size:11px;color:#888;word-break:break-all'>Or copy: " + esc(verifyLink) + "</p>");
    }

    private String buildOrderConfirmationHtml(String name, String orderId, String total, String date) {
        return emailWrapper("Order Confirmed",
                "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Hi <strong>" + esc(name) + "</strong>,</p>"
                + "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Thank you for your order! Here's a summary:</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0'>"
                + "<tr><td style='padding:8px;color:#666;font-size:14px'>Order ID</td><td style='padding:8px;font-weight:600'>#" + esc(orderId) + "</td></tr>"
                + "<tr style='background:#f9f9f9'><td style='padding:8px;color:#666;font-size:14px'>Date</td><td style='padding:8px'>" + esc(date) + "</td></tr>"
                + "<tr><td style='padding:8px;color:#666;font-size:14px'>Total</td><td style='padding:8px;font-weight:600;color:#00897b'>R " + esc(total) + "</td></tr>"
                + "</table>"
                + "<p style='color:#444;font-size:14px'>We'll notify you when your order ships. Thank you for shopping with StoreCraft!</p>");
    }

    private String buildNewOrderSellerHtml(String sellerName, String orderId, String buyerName, String total) {
        return emailWrapper("New Order Received",
                "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>Hi <strong>" + esc(sellerName) + "</strong>,</p>"
                + "<p style='color:#444;font-size:15px;line-height:1.7;margin:0 0 16px'>You have a new order on StoreCraft!</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0'>"
                + "<tr><td style='padding:8px;color:#666;font-size:14px'>Order ID</td><td style='padding:8px;font-weight:600'>#" + esc(orderId) + "</td></tr>"
                + "<tr style='background:#f9f9f9'><td style='padding:8px;color:#666;font-size:14px'>Buyer</td><td style='padding:8px'>" + esc(buyerName) + "</td></tr>"
                + "<tr><td style='padding:8px;color:#666;font-size:14px'>Total</td><td style='padding:8px;font-weight:600;color:#00897b'>R " + esc(total) + "</td></tr>"
                + "</table>"
                + "<p style='color:#444;font-size:14px'>Log in to your seller dashboard to manage this order.</p>");
    }

    // Wraps content in a standard email frame
    private String emailWrapper(String title, String bodyContent) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'/>"
                + "<title>" + esc(title) + "</title></head>"
                + "<body style='margin:0;padding:0;background:#f4f4f7;font-family:Helvetica Neue,Arial,sans-serif'>"
                + "<div style='max-width:580px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,.08)'>"
                + "<div style='background:linear-gradient(135deg,#00897b,#004d40);padding:36px 40px;text-align:center'>"
                + "<h1 style='margin:0;color:#fff;font-size:26px'>StoreCraft</h1>"
                + "<p style='margin:6px 0 0;color:rgba(255,255,255,.8);font-size:14px'>" + esc(title) + "</p>"
                + "</div>"
                + "<div style='padding:40px'>" + bodyContent + "</div>"
                + "<div style='background:#f9f9f9;padding:24px 40px;text-align:center;font-size:12px;color:#aaa;border-top:1px solid #eee'>"
                + "&copy; 2025 StoreCraft &mdash; Authentic African Products"
                + "</div></div></body></html>";
    }

    /** Escapes HTML special characters to prevent XSS in email bodies */
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    /** Serialises a Java string as a JSON string literal (handles newlines/quotes in HTML) */
    private String jsonStringLiteral(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }
}
