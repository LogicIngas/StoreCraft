package com.example.loginpage.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

/**
 * Address Entity - Stores user addresses for shipping and billing
 * One user can have multiple addresses
 */
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @UuidGenerator
    @Column(name = "address_id", updatable = false, nullable = false)
    private String addressId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "type", length = 20)  // SHIPPING, BILLING, DEFAULT
    private String type;

    @Column(name = "recipient_name", length = 100, nullable = false)
    private String recipientName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "street_address", columnDefinition = "TEXT", nullable = false)
    private String streetAddress;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 20, nullable = false)
    private String postalCode;

    @Column(name = "country", length = 100, nullable = false)
    private String country;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Address() {}

    private Address(Builder builder) {
        this.userId = builder.userId;
        this.type = builder.type;
        this.recipientName = builder.recipientName;
        this.phoneNumber = builder.phoneNumber;
        this.streetAddress = builder.streetAddress;
        this.city = builder.city;
        this.stateProvince = builder.stateProvince;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
        this.isDefault = builder.isDefault;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Builder Pattern
    public static class Builder {
        private String userId;
        private String type;
        private String recipientName;
        private String phoneNumber;
        private String streetAddress;
        private String city;
        private String stateProvince;
        private String postalCode;
        private String country;
        private Boolean isDefault = false;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setRecipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setStateProvince(String stateProvince) {
            this.stateProvince = stateProvince;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}