package com.example.loginpage.service.impl;

import com.example.loginpage.model.Address;
import com.example.loginpage.repository.IAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    private final IAddressRepository addressRepository;

    @Autowired
    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Address createAddress(String userId, String type, String recipientName,
                                 String phoneNumber, String streetAddress, String city,
                                 String stateProvince, String postalCode, String country,
                                 Boolean isDefault) {

        if (isDefault != null && isDefault) {
            addressRepository.findByUserId(userId).stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                    .forEach(a -> {
                        a.setIsDefault(false);
                        addressRepository.save(a);
                    });
        }

        Address address = new Address.Builder()
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

        return addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public Address getAddressById(String addressId) {
        return addressRepository.findById(addressId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Address getDefaultAddressByUserId(String userId) {
        return addressRepository.findDefaultAddressByUserId(userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressesByType(String userId, String type) {
        return addressRepository.findByUserIdAndType(userId, type);
    }

    @Transactional
    public Address updateAddress(String addressId, String type, String recipientName,
                                 String phoneNumber, String streetAddress, String city,
                                 String stateProvince, String postalCode, String country,
                                 Boolean isDefault) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        if (isDefault != null && isDefault) {
            addressRepository.findByUserId(address.getUserId()).stream()
                    .filter(a -> !a.getAddressId().equals(addressId) &&
                            Boolean.TRUE.equals(a.getIsDefault()))
                    .forEach(a -> {
                        a.setIsDefault(false);
                        addressRepository.save(a);
                    });
        }

        address.setType(type);
        address.setRecipientName(recipientName);
        address.setPhoneNumber(phoneNumber);
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setStateProvince(stateProvince);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setIsDefault(isDefault);
        address.setUpdatedAt(LocalDateTime.now());

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(String addressId) {
        addressRepository.findById(addressId).ifPresent(address -> {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                addressRepository.findByUserId(address.getUserId()).stream()
                        .filter(a -> !a.getAddressId().equals(addressId))
                        .findFirst()
                        .ifPresent(a -> {
                            a.setIsDefault(true);
                            addressRepository.save(a);
                        });
            }
            addressRepository.delete(address);
        });
    }

    @Transactional
    public Address setAsDefault(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        addressRepository.findByUserId(address.getUserId()).stream()
                .filter(a -> !a.getAddressId().equals(addressId) &&
                        Boolean.TRUE.equals(a.getIsDefault()))
                .forEach(a -> {
                    a.setIsDefault(false);
                    addressRepository.save(a);
                });

        address.setIsDefault(true);
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public boolean userHasAddresses(String userId) {
        return addressRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getAddressCount(String userId) {
        return addressRepository.countByUserId(userId);
    }
}