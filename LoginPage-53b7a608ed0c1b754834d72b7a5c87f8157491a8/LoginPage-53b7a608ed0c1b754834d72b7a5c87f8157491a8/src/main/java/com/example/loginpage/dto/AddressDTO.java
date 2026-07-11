package com.example.loginpage.dto;

import java.time.LocalDateTime;

/**
 * AddressDTO - Data Transfer Object for Address entity
 * Used in API responses to expose address information
 */
public record AddressDTO(
        String addressId,
        String userId,
        String type,           // SHIPPING, BILLING, DEFAULT
        String recipientName,
        String phoneNumber,
        String streetAddress,
        String city,
        String stateProvince,
        String postalCode,
        String country,
        Boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}