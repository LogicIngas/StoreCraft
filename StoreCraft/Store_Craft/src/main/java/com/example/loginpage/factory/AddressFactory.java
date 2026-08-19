package com.example.loginpage.factory;

import com.example.loginpage.model.Address;
import com.example.loginpage.util.Helper;

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

        if (Helper.isNullOrEmpty(userId) || Helper.isNullOrEmpty(postalCode) || Helper.isNullOrEmpty(country) || Helper.isNullOrEmpty(city) || Helper.isNullOrEmpty(recipientName) || Helper.isNullOrEmpty(streetAddress)) {
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