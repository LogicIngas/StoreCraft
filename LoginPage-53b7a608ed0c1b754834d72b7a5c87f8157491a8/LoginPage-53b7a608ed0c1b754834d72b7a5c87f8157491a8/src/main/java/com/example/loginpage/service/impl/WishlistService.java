package com.example.loginpage.service.impl;

import com.example.loginpage.model.Wishlist;
import com.example.loginpage.repository.IWishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final IWishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(IWishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional(readOnly = true)
    public List<Wishlist> getWishlistByUserId(String userId) {
        // ✅ JPA will automatically fetch the Product due to EAGER fetch
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public void addToWishlist(String userId, String productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }
        Wishlist wishlist = new Wishlist(userId, productId);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(String userId, String productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product not in wishlist");
        }
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void removeFromWishlistById(String wishlistId) {
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new RuntimeException("Wishlist item not found");
        }
        wishlistRepository.deleteById(wishlistId);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public long getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}