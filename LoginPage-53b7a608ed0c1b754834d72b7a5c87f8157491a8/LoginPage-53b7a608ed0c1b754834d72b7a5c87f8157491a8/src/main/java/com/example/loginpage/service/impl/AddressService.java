package com.example.loginpage.service.impl;

import com.example.loginpage.dto.AddressDTO;
import com.example.loginpage.model.Address;
import com.example.loginpage.repository.IAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final IAddressRepository addressRepository;

    @Autowired
    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Create new address for user
     */
    @Transactional
    public AddressDTO createAddress(String userId, String type, String recipientName,
                                    String phoneNumber, String streetAddress, String city,
                                    String stateProvince, String postalCode, String country,
                                    Boolean isDefault) {
        System.out.println("📍 Creating address for user: " + userId);

        // Validate input
        validateAddress(recipientName, streetAddress, city, postalCode, country);

        // If setting as default, unset other defaults
        if (isDefault != null && isDefault) {
            addressRepository.findByUserId(userId).stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                    .forEach(a -> {
                        a.setIsDefault(false);
                        addressRepository.save(a);
                    });
        }

        // Create address using builder
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

        Address savedAddress = addressRepository.save(address);
        System.out.println("✅ Address created: " + savedAddress.getAddressId());
        return convertToDTO(savedAddress);
    }

    /**
     * Get address by ID
     */
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(String addressId) {
        return addressRepository.findById(addressId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get all addresses for user
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByUserId(String userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get default address for user
     */
    @Transactional(readOnly = true)
    public AddressDTO getDefaultAddressByUserId(String userId) {
        return addressRepository.findDefaultAddressByUserId(userId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get addresses by type (SHIPPING, BILLING, etc.)
     */
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByType(String userId, String type) {
        return addressRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update address
     */
    @Transactional
    public AddressDTO updateAddress(String addressId, String type, String recipientName,
                                    String phoneNumber, String streetAddress, String city,
                                    String stateProvince, String postalCode, String country,
                                    Boolean isDefault) {
        System.out.println("📍 Updating address: " + addressId);

        return addressRepository.findById(addressId)
                .map(address -> {
                    validateAddress(recipientName, streetAddress, city, postalCode, country);

                    // If setting as default, unset other defaults
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

                    Address updated = addressRepository.save(address);
                    System.out.println("✅ Address updated: " + addressId);
                    return convertToDTO(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));
    }

    /**
     * Delete address
     */
    @Transactional
    public void deleteAddress(String addressId) {
        System.out.println("🗑️ Deleting address: " + addressId);

        addressRepository.findById(addressId).ifPresent(address -> {
            // If deleting default address, set another as default
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
            System.out.println("✅ Address deleted: " + addressId);
        });
    }

    /**
     * Set address as default
     */
    @Transactional
    public AddressDTO setAsDefault(String addressId) {
        System.out.println("⭐ Setting address as default: " + addressId);

        return addressRepository.findById(addressId)
                .map(address -> {
                    // Unset other defaults
                    addressRepository.findByUserId(address.getUserId()).stream()
                            .filter(a -> !a.getAddressId().equals(addressId) &&
                                    Boolean.TRUE.equals(a.getIsDefault()))
                            .forEach(a -> {
                                a.setIsDefault(false);
                                addressRepository.save(a);
                            });

                    // Set this as default
                    address.setIsDefault(true);
                    address.setUpdatedAt(LocalDateTime.now());
                    Address updated = addressRepository.save(address);
                    System.out.println("✅ Address set as default: " + addressId);
                    return convertToDTO(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));
    }

    /**
     * Check if user has addresses
     */
    @Transactional(readOnly = true)
    public boolean userHasAddresses(String userId) {
        return addressRepository.existsByUserId(userId);
    }

    /**
     * Get address count for user
     */
    @Transactional(readOnly = true)
    public long getAddressCount(String userId) {
        return addressRepository.countByUserId(userId);
    }

    /**
     * Validate address fields
     */
    private void validateAddress(String recipientName, String streetAddress, String city,
                                 String postalCode, String country) {
        if (recipientName == null || recipientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Street address is required");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
    }

    /**
     * Convert Address entity to DTO
     */
    private AddressDTO convertToDTO(Address address) {
        return new AddressDTO(
                address.getAddressId(),
                address.getUserId(),
                address.getType(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getStreetAddress(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}