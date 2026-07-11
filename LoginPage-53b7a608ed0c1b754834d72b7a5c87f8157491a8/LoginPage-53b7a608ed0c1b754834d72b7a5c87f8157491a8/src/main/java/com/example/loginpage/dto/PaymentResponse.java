package com.example.loginpage.dto;

/**
 * PaymentResponse - Payment processing result
 * Includes transaction confirmation and order details
 */
public record PaymentResponse(
        boolean success,
        String transactionId,
        String message,
        OrderDTO order  // Order created after successful payment
) {}