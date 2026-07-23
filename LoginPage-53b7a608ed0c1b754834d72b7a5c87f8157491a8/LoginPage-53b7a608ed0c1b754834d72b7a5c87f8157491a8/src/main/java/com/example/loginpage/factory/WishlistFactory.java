package com.example.loginpage.factory;

import com.example.loginpage.model.Wishlist;

public class WishlistFactory {

    public static Wishlist createWishlist(String userId, String productId) {
        if (userId.trim().isEmpty() || productId.trim().isEmpty()) {
            return null;
        }
        return new Wishlist(userId, productId);
    }
}