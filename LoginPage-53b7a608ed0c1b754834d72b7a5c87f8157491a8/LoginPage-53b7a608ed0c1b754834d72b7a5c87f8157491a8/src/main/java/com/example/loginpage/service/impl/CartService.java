package com.example.loginpage.service.impl;

import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Product;
import com.example.loginpage.repository.ICartRepository;
import com.example.loginpage.repository.ICartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductService productService;

    @Autowired
    public CartService(ICartRepository cartRepository, ICartItemRepository cartItemRepository,
                       ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    @Transactional
    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public Cart addToCart(String userId, String productId, Integer quantity) {
        logger.info("=== ADD TO CART ===");
        logger.info("userId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        // Validate product
        Product product = productService.getProductEntity(productId);
        if (product == null) {
            logger.error("Product not found: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }
        logger.info("Product found: {}", product.getName());

        if (product.getStockQuantity() < quantity) {
            logger.error("Insufficient stock. Available: {}, Requested: {}", product.getStockQuantity(), quantity);
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        // Get or create cart
        Cart cart = getOrCreateCart(userId);
        logger.info("Cart ID: {}", cart.getCartId());

        // Check if product already in cart
        CartItem existingItem = null;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getProductId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            // Update existing item
            logger.info("Updating existing cart item. Current quantity: {}", existingItem.getQuantity());
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            logger.info("Creating new cart item");
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setAddedAt(LocalDateTime.now());
            
            // Save the cart item
            CartItem savedItem = cartItemRepository.save(newItem);
            logger.info("Saved cart item ID: {}", savedItem.getCartItemId());
            
            // Add to cart's collection
            cart.getCartItems().add(savedItem);
        }

        // Save and return the cart
        Cart savedCart = cartRepository.save(cart);
        logger.info("Cart saved. Total items: {}", savedCart.getCartItems().size());
        return savedCart;
    }

    @Transactional
    public Cart updateCartItemQuantity(String userId, String cartItemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (quantity <= 0) {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(String userId, String cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cartRepository.save(cart);
        });
    }

    @Transactional(readOnly = true)
    public Integer getCartItemCount(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        return cart != null ? cart.getCartItems().size() : 0;
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) return BigDecimal.ZERO;
        return cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}