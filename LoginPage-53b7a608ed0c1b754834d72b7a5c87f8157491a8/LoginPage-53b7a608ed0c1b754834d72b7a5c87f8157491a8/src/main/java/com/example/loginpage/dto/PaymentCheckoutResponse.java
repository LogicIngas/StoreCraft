package com.example.loginpage.dto;

/**
 * PaymentCheckoutResponse - Enhanced payment response
 * Includes full payment record and payment ID for tracking
 */
public record PaymentCheckoutResponse(
        boolean success,                // Payment success status
        String paymentId,               // Stored payment ID (for tracking)
        String transactionId,           // Gateway transaction ID
        String message,                 // Success/failure message
        PaymentDTO payment              // Full payment record from database
) {}