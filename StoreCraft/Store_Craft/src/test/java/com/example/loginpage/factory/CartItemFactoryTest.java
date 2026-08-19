package com.example.loginpage.factory;

import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Product;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class CartItemFactoryTest {

    @Test
    public void testCreateCartItem_Success() {
        String userId = Helper.generateShortUUID();
        Cart cart = CartFactory.createCart(userId);
        Product product = ProductFactory.createProduct("Laptop", "Gaming laptop", new BigDecimal("1500.00"), 10, "url", "Electronics");
        
        CartItem cartItem = CartItemFactory.createCartItem(cart, product, 2);
        assertNotNull(cartItem);
    }

    @Test
    public void testCreateCartItem_Fail_NullCart() {
        String userId = Helper.generateShortUUID();
        Product product = ProductFactory.createProduct("Laptop", "Gaming laptop", new BigDecimal("1500.00"), 10, "url", "Electronics");
        
        CartItem cartItem = CartItemFactory.createCartItem(null, product, 2);
        assertNull(cartItem);
    }
}
