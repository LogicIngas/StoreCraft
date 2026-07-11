package com.example.loginpage.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String orderItemId,
        String productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}