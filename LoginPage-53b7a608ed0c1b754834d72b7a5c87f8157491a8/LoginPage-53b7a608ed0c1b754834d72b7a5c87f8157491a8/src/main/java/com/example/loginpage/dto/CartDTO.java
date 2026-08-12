package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    private String cartId;
    private String userId;
    private List<CartItemDTO> items;
    private BigDecimal total;

    public CartDTO() {}

    public CartDTO(String cartId, String userId, List<CartItemDTO> items, BigDecimal total) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.total = total;
    }

    // Getters and Setters
    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}