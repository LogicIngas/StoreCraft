package com.example.loginpage.dto;

import java.math.BigDecimal;

/**
 * Response DTO for total amount paid by a user
 */
public record PaymentTotalPaidResponse(
        BigDecimal totalAmount
) {}