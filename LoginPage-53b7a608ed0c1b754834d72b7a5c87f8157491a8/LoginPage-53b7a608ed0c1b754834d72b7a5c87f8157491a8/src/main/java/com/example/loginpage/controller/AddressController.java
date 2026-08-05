package com.example.loginpage.controller;

import com.example.loginpage.dto.*;
import com.example.loginpage.service.impl.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "http://localhost:5173")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Create new address
     * POST /address/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAddress(@RequestBody CreateAddressRequest request) {
        try {
            System.out.println("📍 Creating address for user: " + request.userId());

            // Validate request
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ErrorResponse("User ID is required"));
            }

            AddressDTO address = addressService.createAddress(
                    request.userId(),
                    request.type(),
                    request.recipientName(),
                    request.phoneNumber(),
                    request.streetAddress(),
                    request.city(),
                    request.stateProvince(),
                    request.postalCode(),
                    request.country(),
                    request.isDefault()
            );

            System.out.println("✅ Address created: " + address.addressId());
            return ResponseEntity.ok(address);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error creating address: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to create address: " + e.getMessage()));
        }
    }

    /**
     * Get address by ID
     * GET /address/{addressId}
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<?> getAddressById(@PathVariable String addressId) {
        try {
            AddressDTO address = addressService.getAddressById(addressId);
            if (address == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to retrieve address: " + e.getMessage()));
        }
    }

    /**
     * Get all addresses for user
     * GET /address/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAddressesByUserId(@PathVariable String userId) {
        try {
            List<AddressDTO> addresses = addressService.getAddressesByUserId(userId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to retrieve addresses: " + e.getMessage()));
        }
    }

    /**
     * Get default address for user
     * GET /address/user/{userId}/default
     */
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<?> getDefaultAddress(@PathVariable String userId) {
        try {
            AddressDTO address = addressService.getDefaultAddressByUserId(userId);
            if (address == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to retrieve default address: " + e.getMessage()));
        }
    }

    /**
     * Get addresses by type (SHIPPING, BILLING, etc.)
     * GET /address/user/{userId}/type/{type}
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getAddressesByType(@PathVariable String userId, @PathVariable String type) {
        try {
            List<AddressDTO> addresses = addressService.getAddressesByType(userId, type);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to retrieve addresses: " + e.getMessage()));
        }
    }

    /**
     * Update address
     * PUT /address/{addressId}
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable String addressId,
            @RequestBody UpdateAddressRequest request) {
        try {
            System.out.println("📍 Updating address: " + addressId);

            AddressDTO address = addressService.updateAddress(
                    addressId,
                    request.type(),
                    request.recipientName(),
                    request.phoneNumber(),
                    request.streetAddress(),
                    request.city(),
                    request.stateProvince(),
                    request.postalCode(),
                    request.country(),
                    request.isDefault()
            );

            System.out.println("✅ Address updated: " + addressId);
            return ResponseEntity.ok(address);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error updating address: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to update address: " + e.getMessage()));
        }
    }

    /**
     * Delete address
     * DELETE /address/{addressId}
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable String addressId) {
        try {
            System.out.println("🗑️ Deleting address: " + addressId);
            addressService.deleteAddress(addressId);
            System.out.println("✅ Address deleted: " + addressId);
            return ResponseEntity.ok(new SuccessResponse("Address deleted successfully"));
        } catch (Exception e) {
            System.err.println("❌ Error deleting address: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to delete address: " + e.getMessage()));
        }
    }

    /**
     * Set address as default
     * PUT /address/{addressId}/default
     */
    @PutMapping("/{addressId}/default")
    public ResponseEntity<?> setAsDefault(@PathVariable String addressId) {
        try {
            System.out.println("⭐ Setting address as default: " + addressId);
            AddressDTO address = addressService.setAsDefault(addressId);
            System.out.println("✅ Address set as default: " + addressId);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            System.err.println("❌ Error setting default address: " + e.getMessage());
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to set default address: " + e.getMessage()));
        }
    }

    /**
     * Check if user has addresses
     * GET /address/user/{userId}/exists
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<?> hasAddresses(@PathVariable String userId) {
        try {
            boolean hasAddresses = addressService.userHasAddresses(userId);
            return ResponseEntity.ok(new AddressExistsResponse(hasAddresses));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to check addresses: " + e.getMessage()));
        }
    }

    /**
     * Get address count for user
     * GET /address/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getAddressCount(@PathVariable String userId) {
        try {
            long count = addressService.getAddressCount(userId);
            return ResponseEntity.ok(new AddressCountResponse(count));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse("Failed to get address count: " + e.getMessage()));
        }
    }
}

/**
 * Response wrapper for address exists check
 */
record AddressExistsResponse(boolean exists) {}

/**
 * Response wrapper for address count
 */
record AddressCountResponse(long count) {}