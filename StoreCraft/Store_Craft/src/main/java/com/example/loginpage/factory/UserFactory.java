package com.example.loginpage.factory;

import com.example.loginpage.model.Role;
import com.example.loginpage.model.User;
import com.example.loginpage.util.Helper;

public class UserFactory {

    public static User createUser(
            String email,
            String password,
            String firstName,
            String lastName,
            Role role) {


        if (Helper.isNullOrEmpty(email) || Helper.isNullOrEmpty(password) || role == null) {
            return null;
        }

        return new User.Builder()
                .setUserId(Helper.generateShortUUID())
                .setEmail(email)
                .setPassword(password)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setRole(role)
                .build();
    }
}