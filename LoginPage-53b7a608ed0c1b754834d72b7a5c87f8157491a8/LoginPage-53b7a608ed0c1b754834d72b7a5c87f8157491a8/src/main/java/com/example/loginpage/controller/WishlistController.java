package com.example.loginpage.controller;

import com.example.loginpage.dto.ErrorResponse;
import com.example.loginpage.dto.WishlistDTO;
import com.example.loginpage.service.impl.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")  // Changed to /api/wishlist to avoid conflicts
@CrossOrigin(origins = "http://localhost:5173")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWishlist(@PathVariable String userId) {
        try {
            System.out.println("Getting wishlist for user: " + userId);
            List<WishlistDTO> wishlist = wishlistService.getWishlistByUserId(userId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            System.err.println(" Error getting wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(@RequestBody AddToWishlistRequest request) {
        try {
            System.out.println("➕ Adding to wishlist - User: " + request.userId() + ", Product: " + request.productId());

            if (request.userId() == null || request.userId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
            }
            if (request.productId() == null || request.productId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Product ID is required"));
            }

            wishlistService.addToWishlist(request.userId(), request.productId());
            System.out.println("✅ Added to wishlist successfully");
            return ResponseEntity.ok("Added to wishlist");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error adding to wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error adding to wishlist: " + e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to add to wishlist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromWishlist(@RequestParam String userId, @RequestParam String productId) {
        try {
            System.out.println("🗑️ Removing from wishlist - User: " + userId + ", Product: " + productId);

            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
            }
            if (productId == null || productId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Product ID is required"));
            }

            wishlistService.removeFromWishlist(userId, productId);
            System.out.println("✅ Removed from wishlist successfully");
            return ResponseEntity.ok("Removed from wishlist");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error removing from wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error removing from wishlist: " + e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to remove from wishlist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{wishlistId}")
    public ResponseEntity<?> removeFromWishlistById(@PathVariable String wishlistId) {
        try {
            System.out.println("🗑️ Removing from wishlist by ID: " + wishlistId);
            wishlistService.removeFromWishlistById(wishlistId);
            System.out.println("✅ Removed from wishlist successfully");
            return ResponseEntity.ok("Removed from wishlist");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error removing from wishlist: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error removing from wishlist: " + e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to remove from wishlist: " + e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> isInWishlist(@RequestParam String userId, @RequestParam String productId) {
        try {
            boolean exists = wishlistService.isInWishlist(userId, productId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<?> getWishlistCount(@PathVariable String userId) {
        try {
            long count = wishlistService.getWishlistCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public record AddToWishlistRequest(String userId, String productId) {}
}