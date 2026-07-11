package com.example.loginpage.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
        String reviewId,
        String productId,
        String userId,
        String userName,
        Integer rating,
        String reviewText,
        LocalDateTime createdAt
) {}