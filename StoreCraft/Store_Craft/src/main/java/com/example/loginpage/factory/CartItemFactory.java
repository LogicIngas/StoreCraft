package com.example.loginpage.factory;

import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Product;

public class CartItemFactory {

    public static CartItem createCartItem(Cart cart, Product product, Integer quantity) {

        if (cart == null || product == null || quantity <= 0) {
            return null;
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}