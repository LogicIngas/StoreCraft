package com.example.loginpage.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;

/**
 * B1 / A3 — Updated EmailService.
 *
 * Delegates all sending to {@link BrevoEmailService} (HTTP API primary, SMTP fallback).
 * Keeps the same public method signatures so no other code needs to change.
 *
 * Previously this class autowired JavaMailSender directly and required MAIL_USERNAME /
 * MAIL_PASSWORD which were never configured.  Those props are now replaced by Brevo
 * env vars (BREVO_API_KEY and BREVO_SMTP_*) — see application.properties.
 *
 * The Thymeleaf templates remain for the legacy template-based helpers below, but
 * BrevoEmailService also provides its own inline-CSS builders for the three core
 * transactional flows (reset, verification, order confirmation).
 */
@Service
public class EmailService {

    private final BrevoEmailService brevoEmailService;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:5173}")
    private String appBaseUrl;

    public EmailService(BrevoEmailService brevoEmailService, TemplateEngine templateEngine) {
        this.brevoEmailService = brevoEmailService;
        this.templateEngine = templateEngine;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API (unchanged method signatures — no call-site changes needed)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * A3 / B1 — Forgot-password email.
     * Previously broken because it used unconfigured MAIL_* SMTP properties.
     * Now routed through BrevoEmailService.
     */
    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        try {
            brevoEmailService.sendPasswordReset(to, userName, resetLink);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }

    /** B2 — Email verification. */
    public void sendVerificationEmail(String to, String userName, String verifyLink) {
        try {
            brevoEmailService.sendEmailVerification(to, userName, verifyLink);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    /** B1 — Order confirmation to buyer. */
    public void sendOrderConfirmation(String to, String userName, String orderId,
                                      BigDecimal totalAmount, String orderDate) {
        try {
            brevoEmailService.sendOrderConfirmationBuyer(
                    to, userName, orderId, totalAmount.toPlainString(), orderDate);
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }

    /** B1 — New order notification to seller. */
    public void sendNewOrderNotificationSeller(String to, String sellerName,
                                               String orderId, String buyerName,
                                               BigDecimal total) {
        try {
            brevoEmailService.sendNewOrderNotificationSeller(
                    to, sellerName, orderId, buyerName, total.toPlainString());
        } catch (Exception e) {
            System.err.println("Failed to send seller order notification: " + e.getMessage());
        }
    }

    /** Sends a welcome email using the existing Thymeleaf template. */
    public void sendWelcomeEmail(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("shopName", "StoreCraft");
            context.setVariable("shopUrl", appBaseUrl);
            String html = templateEngine.process("email/welcome", context);
            brevoEmailService.send(to, userName, "Welcome to StoreCraft! 🎉", html);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    /** Sends a payment/shipping notification using the existing Thymeleaf template. */
    public void sendShippingNotification(String to, String userName, String orderId,
                                         String trackingNumber, String estimatedDelivery) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderId", orderId);
            context.setVariable("trackingNumber", trackingNumber);
            context.setVariable("estimatedDelivery", estimatedDelivery);
            context.setVariable("shopName", "StoreCraft");
            String html = templateEngine.process("email/shipping-notification", context);
            brevoEmailService.send(to, userName, "Your Order #" + orderId + " Has Been Shipped!", html);
        } catch (Exception e) {
            System.err.println("Failed to send shipping notification: " + e.getMessage());
        }
    }
}