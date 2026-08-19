package com.example.loginpage.factory;

import com.example.loginpage.model.Order;
import com.example.loginpage.model.OrderItem;
import com.example.loginpage.model.Product;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemFactoryTest {

    @Test
    public void testCreateOrderItem_Success() {
        String userId = Helper.generateShortUUID();
        Order order = OrderFactory.createOrder(userId, new BigDecimal("250.00"), "123 Main St");
        Product product = ProductFactory.createProduct("Mouse", "Wireless", new BigDecimal("25.00"), 50, "url", "Accessories");
        
        OrderItem orderItem = OrderItemFactory.createOrderItem(order, product, 2, new BigDecimal("25.00"));
        assertNotNull(orderItem);
    }

    @Test
    public void testCreateOrderItem_Fail_InvalidQuantity() {
        String userId = Helper.generateShortUUID();
        Order order = OrderFactory.createOrder(userId, new BigDecimal("250.00"), "123 Main St");
        Product product = ProductFactory.createProduct("Mouse", "Wireless", new BigDecimal("25.00"), 50, "url", "Accessories");
        
        OrderItem orderItem = OrderItemFactory.createOrderItem(order, product, 0, new BigDecimal("25.00"));
        assertNull(orderItem);
    }
}
