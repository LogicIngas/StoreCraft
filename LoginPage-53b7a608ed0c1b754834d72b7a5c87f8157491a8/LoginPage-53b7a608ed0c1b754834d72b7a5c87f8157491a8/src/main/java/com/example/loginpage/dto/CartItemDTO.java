package com.example.loginpage.dto;

import java.math.BigDecimal;

public record CartItemDTO(
        String cartItemId,
        String productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {}