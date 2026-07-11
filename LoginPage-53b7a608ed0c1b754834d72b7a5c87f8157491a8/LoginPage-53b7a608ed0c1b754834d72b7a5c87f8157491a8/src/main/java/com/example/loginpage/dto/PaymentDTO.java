package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentDTO - Data Transfer Object for Payment entity
 * Used in API responses to expose payment information
 */
public record PaymentDTO(
        String paymentId,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String transactionId,
        String status,
        String cardLast4,
        String cardBrand,
        String cardHolderName,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}