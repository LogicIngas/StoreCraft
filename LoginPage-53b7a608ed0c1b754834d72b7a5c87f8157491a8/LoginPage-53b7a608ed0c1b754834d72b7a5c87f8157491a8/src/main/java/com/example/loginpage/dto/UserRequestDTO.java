package com.example.loginpage.dto;

public record UserRequestDTO(
        String email,
        String password,
        String firstName,
        String lastName
) {}
