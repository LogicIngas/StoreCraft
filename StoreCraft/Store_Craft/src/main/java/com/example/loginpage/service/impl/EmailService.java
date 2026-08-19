package com.example.loginpage.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmation(String to, String userName, String orderId,
                                      BigDecimal totalAmount, String orderDate) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderId", orderId);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("orderDate", orderDate);
            context.setVariable("shopName", "AfriConnect");
            context.setVariable("shopUrl", "http://localhost:5173");

            String htmlContent = templateEngine.process("email/order-confirmation", context);
            sendHtmlEmail(to, "Order Confirmation - #" + orderId, htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }

    public void sendPaymentConfirmation(String to, String userName, String paymentId,
                                        BigDecimal amount, String transactionId) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("paymentId", paymentId);
            context.setVariable("amount", amount);
            context.setVariable("transactionId", transactionId);
            context.setVariable("paymentDate", java.time.LocalDateTime.now().toString());
            context.setVariable("shopName", "AfriConnect");

            String htmlContent = templateEngine.process("email/payment-confirmation", context);
            sendHtmlEmail(to, "Payment Confirmation - " + transactionId, htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send payment confirmation email: " + e.getMessage());
        }
    }

    public void sendShippingNotification(String to, String userName, String orderId,
                                         String trackingNumber, String estimatedDelivery) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("orderId", orderId);
            context.setVariable("trackingNumber", trackingNumber);
            context.setVariable("estimatedDelivery", estimatedDelivery);
            context.setVariable("shopName", "AfriConnect");

            String htmlContent = templateEngine.process("email/shipping-notification", context);
            sendHtmlEmail(to, "Your Order #" + orderId + " Has Been Shipped!", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send shipping notification: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String userName) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("shopName", "AfriConnect");
            context.setVariable("shopUrl", "http://localhost:5173");

            String htmlContent = templateEngine.process("email/welcome", context);
            sendHtmlEmail(to, "Welcome to AfriConnect! 🎉", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetLink", resetLink);
            context.setVariable("shopName", "AfriConnect");

            String htmlContent = templateEngine.process("email/password-reset", context);
            sendHtmlEmail(to, "Password Reset Request", htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        System.out.println("📧 Email sent to: " + to);
    }
}