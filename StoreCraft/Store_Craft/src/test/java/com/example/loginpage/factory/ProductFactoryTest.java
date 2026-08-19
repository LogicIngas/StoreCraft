package com.example.loginpage.factory;

import com.example.loginpage.model.Product;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProductFactoryTest {

    @Test
    public void testCreateProduct_Success() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Product product = ProductFactory.createProduct("Smartphone", "Latest model", new BigDecimal("999.99"), 100, "img.jpg", "Electronics");
        assertNotNull(product);
    }

    @Test
    public void testCreateProduct_Fail_EmptyName() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Product product = ProductFactory.createProduct("", "Latest model", new BigDecimal("999.99"), 100, "img.jpg", "Electronics");
        assertNull(product);
    }
}
