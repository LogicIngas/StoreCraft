package com.example.loginpage.controller;

import com.example.loginpage.model.Address;
import com.example.loginpage.service.impl.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Address createAddress(@RequestBody CreateAddressRequest request) {
        return service.createAddress(
                request.userId,
                request.type,
                request.recipientName,
                request.phoneNumber,
                request.streetAddress,
                request.city,
                request.stateProvince,
                request.postalCode,
                request.country,
                request.isDefault
        );
    }

    @GetMapping("/{addressId}")
    public Address getAddress(@PathVariable String addressId) {
        return service.getAddressById(addressId);
    }

    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUser(@PathVariable String userId) {
        return service.getAddressesByUserId(userId);
    }

    @GetMapping("/user/{userId}/default")
    public Address getDefaultAddress(@PathVariable String userId) {
        return service.getDefaultAddressByUserId(userId);
    }

    @GetMapping("/user/{userId}/type/{type}")
    public List<Address> getAddressesByType(@PathVariable String userId, @PathVariable String type) {
        return service.getAddressesByType(userId, type);
    }

    @PutMapping("/{addressId}")
    public Address updateAddress(@PathVariable String addressId, @RequestBody UpdateAddressRequest request) {
        return service.updateAddress(
                addressId,
                request.type,
                request.recipientName,
                request.phoneNumber,
                request.streetAddress,
                request.city,
                request.stateProvince,
                request.postalCode,
                request.country,
                request.isDefault
        );
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable String addressId) {
        service.deleteAddress(addressId);
    }

    @PutMapping("/{addressId}/default")
    public Address setAsDefault(@PathVariable String addressId) {
        return service.setAsDefault(addressId);
    }

    @GetMapping("/user/{userId}/exists")
    public boolean hasAddresses(@PathVariable String userId) {
        return service.userHasAddresses(userId);
    }

    @GetMapping("/user/{userId}/count")
    public long getAddressCount(@PathVariable String userId) {
        return service.getAddressCount(userId);
    }

    public static class CreateAddressRequest {
        public String userId;
        public String type;
        public String recipientName;
        public String phoneNumber;
        public String streetAddress;
        public String city;
        public String stateProvince;
        public String postalCode;
        public String country;
        public Boolean isDefault;
    }

    public static class UpdateAddressRequest {
        public String type;
        public String recipientName;
        public String phoneNumber;
        public String streetAddress;
        public String city;
        public String stateProvince;
        public String postalCode;
        public String country;
        public Boolean isDefault;
    }
}