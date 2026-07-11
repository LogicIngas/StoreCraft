package com.example.loginpage.dto;

public record UpdateCartItemRequest(
        String userId,
        Integer quantity
) {}