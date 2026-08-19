package com.example.loginpage.factory;

import com.example.loginpage.model.Order;
import com.example.loginpage.util.Helper;

import java.math.BigDecimal;

public class OrderFactory {

    public static Order createOrder(
            String userId,
            BigDecimal totalAmount,
            String shippingAddress) {

        if (Helper.isNullOrEmpty(userId) || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0 || Helper.isNullOrEmpty(shippingAddress)) {
            return null;
        }

        return new Order(userId, totalAmount, shippingAddress);
    }
}