package com.example.loginpage.factory;

import com.example.loginpage.model.Wishlist;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WishlistFactoryTest {

    @Test
    public void testCreateWishlist_Success() {
        String userId = Helper.generateShortUUID();
        Wishlist wishlist = WishlistFactory.createWishlist(userId, "PROD123");
        assertNotNull(wishlist);
    }

    @Test
    public void testCreateWishlist_Fail_EmptyProductId() {
        String userId = Helper.generateShortUUID();
        Wishlist wishlist = WishlistFactory.createWishlist(userId, "");
        assertNull(wishlist);
    }
}
