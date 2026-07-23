package com.example.loginpage.factory;

import com.example.loginpage.model.Address;

public class AddressFactory {

    public static Address createAddress(
            String userId,
            String type,
            String recipientName,
            String phoneNumber,
            String streetAddress,
            String city,
            String stateProvince,
            String postalCode,
            String country,
            Boolean isDefault) {

        // Return null if any required field is invalid
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        if (recipientName == null || recipientName.trim().isEmpty()) {
            return null;
        }
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            return null;
        }
        if (city == null || city.trim().isEmpty()) {
            return null;
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return null;
        }
        if (country == null || country.trim().isEmpty()) {
            return null;
        }

        return new Address.Builder()
                .setUserId(userId)
                .setType(type != null ? type : "SHIPPING")
                .setRecipientName(recipientName)
                .setPhoneNumber(phoneNumber)
                .setStreetAddress(streetAddress)
                .setCity(city)
                .setStateProvince(stateProvince)
                .setPostalCode(postalCode)
                .setCountry(country)
                .setIsDefault(isDefault != null ? isDefault : false)
                .build();
    }
}