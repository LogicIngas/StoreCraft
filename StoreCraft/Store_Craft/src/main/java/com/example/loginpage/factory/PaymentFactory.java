package com.example.loginpage.factory;

import com.example.loginpage.model.Payment;
import com.example.loginpage.util.Helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentFactory {

    public static Payment createPayment(
            String orderId,
            String userId,
            BigDecimal amount,
            String currency,
            String paymentMethod,
            String cardLast4,
            String cardHolderName) {

        // Return null if any required field is invalid
        if (Helper.isNullOrEmpty(orderId) || Helper.isNullOrEmpty(userId) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "ZAR");
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "MOCK");
        payment.setTransactionId("TXN-" + Helper.generateShortUUID().toUpperCase());
        payment.setStatus("PROCESSING");
        payment.setCardLast4(cardLast4 != null && !cardLast4.isEmpty() ? cardLast4 : "****");
        payment.setCardBrand("MOCK");
        payment.setCardHolderName(cardHolderName != null && !cardHolderName.isEmpty() ? cardHolderName : "Mock User");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return payment;
    }
}