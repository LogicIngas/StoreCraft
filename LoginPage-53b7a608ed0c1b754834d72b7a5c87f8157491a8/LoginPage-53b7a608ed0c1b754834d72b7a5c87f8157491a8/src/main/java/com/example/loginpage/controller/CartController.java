package com.example.loginpage.controller;

import com.example.loginpage.dto.AddToCartRequest;
import com.example.loginpage.dto.CartCountResponse;
import com.example.loginpage.dto.CartDTO;
import com.example.loginpage.dto.ErrorResponse;
import com.example.loginpage.dto.SuccessResponse;
import com.example.loginpage.dto.UpdateCartItemRequest;
import com.example.loginpage.service.impl.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Get cart for user
     * GET /cart/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
        }
        CartDTO cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    /**
     * Add item to cart
     * POST /cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        try {
            if (request.productId() == null || request.productId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Product ID is required"));
            }
            if (request.quantity() == null || request.quantity() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Quantity must be greater than 0"));
            }

            String userId = request.userId();
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
            }

            System.out.println("🛒 Adding to cart - User: " + userId + ", Product: " + request.productId() + ", Qty: " + request.quantity());

            CartDTO cart = cartService.addToCart(userId, request.productId(), request.quantity());
            System.out.println("✅ Cart updated successfully");

            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error adding to cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update cart item quantity
     * PUT /cart/update/{cartItemId}
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable String cartItemId,
            @RequestBody UpdateCartItemRequest request) {
        try {
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
            }
            if (request.quantity() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Quantity is required"));
            }

            CartDTO cart = cartService.updateCartItemQuantity(
                    request.userId(),
                    cartItemId,
                    request.quantity()
            );
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Remove item from cart
     * DELETE /cart/remove/{cartItemId}
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable String cartItemId,
            @RequestParam String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
            }

            CartDTO cart = cartService.removeFromCart(userId, cartItemId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Clear entire cart
     * DELETE /cart/clear/{userId}
     */
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
        }
        cartService.clearCart(userId);
        return ResponseEntity.ok(new SuccessResponse("Cart cleared successfully"));
    }

    /**
     * Get cart item count
     * GET /cart/count/{userId}
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<?> getCartItemCount(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User ID is required"));
        }
        Integer count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(new CartCountResponse(count));
    }
}