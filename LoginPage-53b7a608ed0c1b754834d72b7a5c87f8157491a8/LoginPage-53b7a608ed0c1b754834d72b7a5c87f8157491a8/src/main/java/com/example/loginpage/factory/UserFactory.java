package com.example.loginpage.factory;

import com.example.loginpage.model.Role;
import com.example.loginpage.model.User;

public class UserFactory {

    public static User createUser(
            String email,
            String password,
            String firstName,
            String lastName,
            Role role) {

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        if (role == null) {
            return null;
        }

        return new User.Builder()
                .setEmail(email)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRole(role)
                .build();
    }
}