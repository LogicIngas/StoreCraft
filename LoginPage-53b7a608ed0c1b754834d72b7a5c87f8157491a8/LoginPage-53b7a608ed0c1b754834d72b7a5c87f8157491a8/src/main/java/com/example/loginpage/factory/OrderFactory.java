package com.example.loginpage.factory;

import com.example.loginpage.model.Order;

import java.math.BigDecimal;

public class OrderFactory {

    public static Order createOrder(
            String userId,
            BigDecimal totalAmount,
            String shippingAddress) {

        if (userId == null || userId.trim().isEmpty() || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0|| shippingAddress.trim().isEmpty()) {
            return null;
        }

        return new Order(userId, totalAmount, shippingAddress);
    }
}