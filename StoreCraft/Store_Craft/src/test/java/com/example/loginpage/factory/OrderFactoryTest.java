package com.example.loginpage.factory;

import com.example.loginpage.model.Order;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class OrderFactoryTest {

    @Test
    public void testCreateOrder_Success() {
        String userId = Helper.generateShortUUID();
        Order order = OrderFactory.createOrder(userId, new BigDecimal("250.00"), "123 Main St, City");
        assertNotNull(order);
    }

    @Test
    public void testCreateOrder_Fail_InvalidAmount() {
        String userId = Helper.generateShortUUID();
        Order order = OrderFactory.createOrder(userId, new BigDecimal("-10.00"), "123 Main St, City");
        assertNull(order);
    }
}
