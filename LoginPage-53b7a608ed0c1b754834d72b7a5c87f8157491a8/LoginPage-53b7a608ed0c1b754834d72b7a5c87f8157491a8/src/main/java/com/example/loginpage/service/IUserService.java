package com.example.loginpage.service;

import com.example.loginpage.model.User;

public interface IUserService extends IService<User, String> {

    /**
     * Find a user by email address
     * Used during login verification
     *
     * @param email the user's email
     * @return User if found, null otherwise
     */
    User findByEmail(String email);
}