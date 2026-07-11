package com.example.loginpage.dto;

/**
 * CreateAddressRequest - Request body for creating new address
 */
public record CreateAddressRequest(
        String userId,
        String type,           // SHIPPING, BILLING, DEFAULT
        String recipientName,
        String phoneNumber,
        String streetAddress,
        String city,
        String stateProvince,
        String postalCode,
        String country,
        Boolean isDefault
) {}