package com.example.loginpage.factory;

import com.example.loginpage.model.Role;

public class RoleFactory {

    public static Role createRole(String name, String description) {
        if (name.trim().isEmpty()) {
            return null;
        }

        return new Role(name, description);
    }

    public static Role createBuyerRole() {
        return new Role("BUYER", "Regular buyer/customer role");
    }

    public static Role createSellerRole() {
        return new Role("SELLER", "Seller who can upload products");
    }

    public static Role createAdminRole() {
        return new Role("ADMIN", "Administrator role");
    }
}