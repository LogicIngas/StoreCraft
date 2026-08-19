package com.example.loginpage.dto;

/**
 * Error response DTO for payment failures
 */
public record PaymentErrorResponse(
        String message
) {}