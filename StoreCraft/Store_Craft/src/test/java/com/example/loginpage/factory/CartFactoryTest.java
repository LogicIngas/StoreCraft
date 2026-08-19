package com.example.loginpage.factory;

import com.example.loginpage.model.Cart;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartFactoryTest {

    @Test
    public void testCreateCart_Success() {
        String userId = Helper.generateShortUUID();
        Cart cart = CartFactory.createCart(userId);
        assertNotNull(cart);
    }

    @Test
    public void testCreateCart_Fail_EmptyUserId() {
        String userId = "";
        Cart cart = CartFactory.createCart(userId);
        assertNull(cart);
    }
}
