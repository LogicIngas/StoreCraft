package com.example.loginpage.factory;

import com.example.loginpage.model.Cart;

public class CartFactory {

    public static Cart createCart(String userId) {
        if (userId.trim().isEmpty()) {
            return null;
        }

        return new Cart(userId);
    }
}