package com.example.loginpage.factory;

import com.example.loginpage.model.Wishlist;
import com.example.loginpage.util.Helper;

public class WishlistFactory {

    public static Wishlist createWishlist(String userId, String productId) {
        if (Helper.isNullOrEmpty(userId) || Helper.isNullOrEmpty(productId)) {
            return null;
        }
        return new Wishlist(userId, productId);
    }
}