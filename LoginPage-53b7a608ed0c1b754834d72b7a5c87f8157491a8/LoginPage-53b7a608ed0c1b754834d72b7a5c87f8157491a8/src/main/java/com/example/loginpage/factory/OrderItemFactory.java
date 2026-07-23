package com.example.loginpage.factory;

import com.example.loginpage.model.Order;
import com.example.loginpage.model.OrderItem;
import com.example.loginpage.model.Product;

import java.math.BigDecimal;

public class OrderItemFactory {

    public static OrderItem createOrderItem(
            Order order, //can comment out this one if its giving you errors during tests / create it object first
            Product product,
            Integer quantity,
            BigDecimal unitPrice) {

        if (order == null || product == null || quantity <= 0|| unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return new OrderItem(order, product, quantity, unitPrice);
    }
}