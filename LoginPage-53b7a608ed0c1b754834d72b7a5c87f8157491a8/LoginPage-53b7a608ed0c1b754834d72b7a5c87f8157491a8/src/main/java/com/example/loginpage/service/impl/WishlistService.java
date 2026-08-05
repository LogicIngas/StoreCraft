package com.example.loginpage.service.impl;

import com.example.loginpage.dto.WishlistDTO;
import com.example.loginpage.model.Product;
import com.example.loginpage.model.Wishlist;
import com.example.loginpage.repository.IProductRepository;
import com.example.loginpage.repository.IWishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final IWishlistRepository wishlistRepository;
    private final IProductRepository productRepository;

    @Autowired
    public WishlistService(IWishlistRepository wishlistRepository, IProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<WishlistDTO> getWishlistByUserId(String userId) {
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(wishlist -> {
                    Product product = productRepository.findById(wishlist.getProductId()).orElse(null);
                    if (product == null) {
                        return null;
                    }
                    return new WishlistDTO(
                            wishlist.getWishlistId(),
                            product.getProductId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl(),
                            wishlist.getCreatedAt()
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(String userId, String productId) {
        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist(userId, productId);
        wishlistRepository.save(wishlist);
        System.out.println("✅ Added to wishlist - User: " + userId + ", Product: " + productId);
    }

    @Transactional
    public void removeFromWishlist(String userId, String productId) {
        System.out.println("🔍 Removing from wishlist - User: " + userId + ", Product: " + productId);

        // Check if exists before deleting
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Product not in wishlist");
        }

        int deleted = wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        System.out.println("✅ Deleted " + deleted + " record(s) from wishlist");
    }

    @Transactional
    public void removeFromWishlistById(String wishlistId) {
        System.out.println("🔍 Removing from wishlist by ID: " + wishlistId);

        if (!wishlistRepository.existsById(wishlistId)) {
            throw new IllegalArgumentException("Wishlist item not found");
        }

        wishlistRepository.deleteById(wishlistId);
        System.out.println("✅ Deleted wishlist item: " + wishlistId);
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