package com.example.loginpage.dto;

/**
 * Response DTO for payment verification
 */
public record PaymentVerifyResponse(
        boolean verified,
        String paymentId,
        String message
) {}