package com.example.loginpage.dto;

/**
 * PaymentRequest - Mock payment processing request
 * In production: would include real card/payment details (encrypted)
 */
public record PaymentRequest(
        String userId,
        String shippingAddress,
        String cardToken,  // Mock token (not real card data)
        String cardLast4   // Last 4 digits for display (mock)
) {}