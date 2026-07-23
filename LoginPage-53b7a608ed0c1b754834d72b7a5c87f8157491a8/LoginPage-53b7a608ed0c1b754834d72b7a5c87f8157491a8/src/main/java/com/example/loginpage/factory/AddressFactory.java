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


        if (userId.trim().isEmpty()|| postalCode.trim().isEmpty() || country.trim().isEmpty()|| city.trim().isEmpty() || recipientName.trim().isEmpty()|| streetAddress.trim().isEmpty()) {
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