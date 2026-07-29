package com.example.loginpage.controller;

import com.example.loginpage.model.Cart;
import com.example.loginpage.service.impl.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable String userId) {
        return service.getCartByUserId(userId);
    }

    @PostMapping("/add")
    public Cart addToCart(@RequestBody AddToCartRequest request) {
        return service.addToCart(request.userId, request.productId, request.quantity);
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