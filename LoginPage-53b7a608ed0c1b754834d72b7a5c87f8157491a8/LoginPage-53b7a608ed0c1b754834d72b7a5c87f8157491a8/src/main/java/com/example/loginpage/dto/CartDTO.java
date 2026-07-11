package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartDTO(
        String cartId,
        String userId,
        List<CartItemDTO> items,
        BigDecimal total,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}