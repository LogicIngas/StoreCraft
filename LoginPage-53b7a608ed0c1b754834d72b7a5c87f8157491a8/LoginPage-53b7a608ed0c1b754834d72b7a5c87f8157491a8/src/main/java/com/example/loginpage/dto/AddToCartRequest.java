package com.example.loginpage.dto;

public record AddToCartRequest(
        String userId,
        String productId,
        Integer quantity
) {}