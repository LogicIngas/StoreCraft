package com.example.loginpage.factory;

import com.example.loginpage.model.Cart;
import com.example.loginpage.util.Helper;

public class CartFactory {

    public static Cart createCart(String userId) {
        if (Helper.isNullOrEmpty(userId)) {
            return null;
        }

        return new Cart(userId);
    }
}