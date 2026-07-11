package com.example.loginpage.factory;

import com.example.loginpage.model.User;

public class UserFactory {

    public static User createUser(
            String email,
            String password,
            String firstName,
            String lastName) {

        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        // REMOVE manual UUID generation here. 
        return new User.Builder()
                .setEmail(email)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .build();
    }
}
