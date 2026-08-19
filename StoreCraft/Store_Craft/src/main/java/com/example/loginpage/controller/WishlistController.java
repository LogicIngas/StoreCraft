package com.example.loginpage.controller;

import com.example.loginpage.dto.WishlistDTO;
import com.example.loginpage.model.Product;
import com.example.loginpage.model.Wishlist;
import com.example.loginpage.service.impl.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class WishlistController {

    private final WishlistService service;

    public WishlistController(WishlistService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public List<WishlistDTO> getWishlist(@PathVariable String userId) {
        List<Wishlist> wishlists = service.getWishlistByUserId(userId);
        return wishlists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public void addToWishlist(@RequestBody AddRequest request) {
        service.addToWishlist(request.userId, request.productId);
    }

    @DeleteMapping("/remove")
    public void removeFromWishlist(@RequestParam String userId, @RequestParam String productId) {
        service.removeFromWishlist(userId, productId);
    }

    @DeleteMapping("/remove/{wishlistId}")
    public void removeFromWishlistById(@PathVariable String wishlistId) {
        service.removeFromWishlistById(wishlistId);
    }

    @GetMapping("/check")
    public boolean isInWishlist(@RequestParam String userId, @RequestParam String productId) {
        return service.isInWishlist(userId, productId);
    }

    @GetMapping("/count/{userId}")
    public long getWishlistCount(@PathVariable String userId) {
        return service.getWishlistCount(userId);
    }

    // --- Helper conversion ---
    private WishlistDTO convertToDTO(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        return new WishlistDTO(
                wishlist.getWishlistId(),
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getCategory(),
                wishlist.getCreatedAt().toString()
        );
    }

    // --- Request DTOs ---
    public static class AddRequest {
        public String userId;
        public String productId;
    }
}