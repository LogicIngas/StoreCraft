package com.example.loginpage.controller;

import com.example.loginpage.dto.CartDTO;
import com.example.loginpage.dto.CartItemDTO;
import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.service.impl.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    public CartDTO getCart(@PathVariable String userId) {
        logger.info("Getting cart for user: {}", userId);
        Cart cart = service.getCartByUserId(userId);
        return convertToCartDTO(cart);
    }

    @PostMapping("/add")
    public CartDTO addToCart(@RequestBody AddToCartRequest request) {
        logger.info("Adding to cart: userId={}, productId={}, quantity={}",
                request.userId, request.productId, request.quantity);
        Cart cart = service.addToCart(request.userId, request.productId, request.quantity);
        return convertToCartDTO(cart);
    }

    @PutMapping("/update/{cartItemId}")
    public CartDTO updateCartItem(@PathVariable String cartItemId, @RequestBody UpdateRequest request) {
        Cart cart = service.updateCartItemQuantity(request.userId, cartItemId, request.quantity);
        return convertToCartDTO(cart);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public CartDTO removeFromCart(@PathVariable String cartItemId, @RequestParam String userId) {
        Cart cart = service.removeFromCart(userId, cartItemId);
        return convertToCartDTO(cart);
    }

    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable String userId) {
        service.clearCart(userId);
    }

    @GetMapping("/count/{userId}")
    public Integer getCartCount(@PathVariable String userId) {
        return service.getCartItemCount(userId);
    }

    // --- Helper conversion ---
    private CartDTO convertToCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(item -> new CartItemDTO(
                        item.getCartItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        BigDecimal total = itemDTOs.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(cart.getCartId(), cart.getUserId(), itemDTOs, total);
    }

    // --- Request DTOs (inner classes) ---
    public static class AddToCartRequest {
        public String userId;
        public String productId;
        public Integer quantity;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class UpdateRequest {
        public String userId;
        public Integer quantity;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}