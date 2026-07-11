package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDTO(
        String productId,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}