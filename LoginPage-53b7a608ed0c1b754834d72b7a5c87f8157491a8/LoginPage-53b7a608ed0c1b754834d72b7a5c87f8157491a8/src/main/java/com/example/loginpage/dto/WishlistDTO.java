package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistDTO(
        String wishlistId,
        String productId,
        String productName,
        BigDecimal price,
        String imageUrl,
        LocalDateTime createdAt
) {}