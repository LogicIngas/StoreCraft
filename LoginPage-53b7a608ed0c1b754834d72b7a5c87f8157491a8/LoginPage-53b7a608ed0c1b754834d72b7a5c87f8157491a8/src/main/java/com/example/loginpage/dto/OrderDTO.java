package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        String orderId,
        String userId,
        List<OrderItemDTO> items,
        BigDecimal totalAmount,
        String status,
        String shippingAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}