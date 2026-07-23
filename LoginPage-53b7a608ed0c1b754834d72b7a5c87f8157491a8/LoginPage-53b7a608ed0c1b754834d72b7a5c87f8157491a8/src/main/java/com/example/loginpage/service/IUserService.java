package com.example.loginpage.service;

import com.example.loginpage.model.User;

public interface IUserService extends IService<User, String> {

    /**
     * Find user by email
     * Used during login verification
     */
    User findByEmail(String email);
}