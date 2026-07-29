package com.example.loginpage.controller;

import com.example.loginpage.model.Cart;
import com.example.loginpage.service.impl.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable String userId) {
        logger.info("Getting cart for user: {}", userId);
        return service.getCartByUserId(userId);
    }

    @PostMapping("/add")
    public Cart addToCart(@RequestBody AddToCartRequest request) {
        logger.info("=== RECEIVED ADD TO CART REQUEST ===");
        logger.info("userId: {}, productId: {}, quantity: {}", 
            request.userId, request.productId, request.quantity);
        
        try {
            Cart result = service.addToCart(request.userId, request.productId, request.quantity);
            logger.info("Successfully added to cart. Cart ID: {}", result.getCartId());
            return result;
        } catch (Exception e) {
            logger.error("Error adding to cart: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/update/{cartItemId}")
    public Cart updateCartItem(@PathVariable String cartItemId, @RequestBody UpdateRequest request) {
        return service.updateCartItemQuantity(request.userId, cartItemId, request.quantity);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public Cart removeFromCart(@PathVariable String cartItemId, @RequestParam String userId) {
        return service.removeFromCart(userId, cartItemId);
    }

    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable String userId) {
        service.clearCart(userId);
    }

    @GetMapping("/count/{userId}")
    public Integer getCartCount(@PathVariable String userId) {
        return service.getCartItemCount(userId);
    }

    public static class AddToCartRequest {
        public String userId;
        public String productId;
        public Integer quantity;
    }

    public static class UpdateRequest {
        public String userId;
        public Integer quantity;
    }
}