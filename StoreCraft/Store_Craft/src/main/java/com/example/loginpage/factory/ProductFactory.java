package com.example.loginpage.factory;

import com.example.loginpage.model.Product;
import com.example.loginpage.util.Helper;

import java.math.BigDecimal;

public class ProductFactory {

    public static Product createProduct(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            String imageUrl,
            String category) {

        if (Helper.isNullOrEmpty(name) || Helper.isNullOrEmpty(category) || stockQuantity == null || stockQuantity < 0 || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return new Product(name, description, price, stockQuantity, imageUrl, category);
    }
}