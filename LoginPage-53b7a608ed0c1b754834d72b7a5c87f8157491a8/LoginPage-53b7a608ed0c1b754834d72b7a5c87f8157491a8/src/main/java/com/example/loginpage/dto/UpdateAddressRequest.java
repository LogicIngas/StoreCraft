package com.example.loginpage.dto;

/**
 * UpdateAddressRequest - Request body for updating address
 */
public record UpdateAddressRequest(
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