package com.example.loginpage.dto;

public record UserResponseDTO(
        String userId,
        String email,
        String firstName,
        String lastName
) {}
