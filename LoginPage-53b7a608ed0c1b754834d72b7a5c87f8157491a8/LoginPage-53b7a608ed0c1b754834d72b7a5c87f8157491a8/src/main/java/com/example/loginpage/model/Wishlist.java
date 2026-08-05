package com.example.loginpage.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @UuidGenerator
    @Column(name = "wishlist_id", updatable = false, nullable = false)
    private String wishlistId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wishlist() {}

    public Wishlist(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
        this.createdAt = LocalDateTime.now();
    }

    public String getWishlistId() { return wishlistId; }
    public void setWishlistId(String wishlistId) { this.wishlistId = wishlistId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}