package com.example.loginpage.dto;

/**
 * PaymentCheckoutRequest - Enhanced payment checkout request
 * Includes cardholder details for database storage
 */
public record PaymentCheckoutRequest(
        String userId,                  // User placing order (required)
        String shippingAddress,         // Delivery address (required)
        String cardToken,               // Mock payment token (required)
        String cardLast4,               // Last 4 digits (for display)
        String cardHolderName           // Name on card (for receipt)
) {}