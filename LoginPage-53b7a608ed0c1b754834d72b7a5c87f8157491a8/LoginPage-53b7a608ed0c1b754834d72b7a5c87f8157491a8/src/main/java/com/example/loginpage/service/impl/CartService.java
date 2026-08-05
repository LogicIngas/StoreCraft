package com.example.loginpage.service.impl;

import com.example.loginpage.dto.CartDTO;
import com.example.loginpage.dto.CartItemDTO;
import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Product;
import com.example.loginpage.repository.ICartItemRepository;
import com.example.loginpage.repository.ICartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;  // ✅ Add this
    private final ProductService productService;

    @Autowired
    public CartService(ICartRepository cartRepository, ICartItemRepository cartItemRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;  // ✅ Initialize
        this.productService = productService;
    }

    /**
     * Get or create cart for user
     */
    @Transactional
    public Cart getOrCreateCart(String userId) {
        System.out.println("🛒 Getting or creating cart for user: " + userId);
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    System.out.println("🛒 Creating new cart for user: " + userId);
                    Cart newCart = new Cart(userId);
                    Cart savedCart = cartRepository.save(newCart);
                    System.out.println("✅ Cart created with ID: " + savedCart.getCartId());
                    return savedCart;
                });
    }

    /**
     * Get cart by user ID
     */
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(String userId) {
        System.out.println("🔍 Looking for cart for user: " + userId);
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            System.out.println("⚠️ No cart found for user: " + userId);
            return null;
        }
        System.out.println("✅ Cart found with " + cart.getCartItems().size() + " items");
        return convertToDTO(cart);
    }

    /**
     * Add product to cart - ✅ FIXED VERSION
     */
    @Transactional
    public CartDTO addToCart(String userId, String productId, Integer quantity) {
        System.out.println("➕ Adding to cart - User: " + userId + ", Product: " + productId + ", Qty: " + quantity);

        // Validate product exists and has stock
        Product product = productService.getProductEntity(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        if (!productService.hasStock(productId, quantity)) {
            throw new IllegalArgumentException("Insufficient stock for product: " + productId);
        }

        // Get or create cart
        Cart cart = getOrCreateCart(userId);
        System.out.println("📦 Cart ID: " + cart.getCartId());

        // Check if product already in cart
        var existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            System.out.println("🔄 Updated existing item quantity to: " + item.getQuantity());
            // ✅ Save the updated cart item
            cartItemRepository.save(item);
        } else {
            // ✅ Create new CartItem with proper references
            CartItem newItem = new CartItem();
            newItem.setCart(cart);  // ✅ Set the cart reference
            newItem.setProduct(product);  // ✅ Set the product reference
            newItem.setQuantity(quantity);
            newItem.setAddedAt(java.time.LocalDateTime.now());

            // ✅ Add to cart's collection
            cart.getCartItems().add(newItem);

            // ✅ Save the cart item first
            CartItem savedItem = cartItemRepository.save(newItem);
            System.out.println("➕ Added new cart item with ID: " + savedItem.getCartItemId());
        }

        // ✅ Save the cart to update the relationship
        Cart savedCart = cartRepository.save(cart);
        System.out.println("💾 Cart saved with " + savedCart.getCartItems().size() + " items");

        // ✅ Verify items were saved
        System.out.println("📋 Cart items in saved cart:");
        savedCart.getCartItems().forEach(item ->
                System.out.println("   - " + item.getProduct().getName() + " x " + item.getQuantity())
        );

        return convertToDTO(savedCart);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public CartDTO updateCartItemQuantity(String userId, String cartItemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));

        if (quantity <= 0) {
            cart.removeCartItem(item);
            cartItemRepository.delete(item);  // ✅ Explicitly delete
        } else {
            if (!productService.hasStock(item.getProduct().getProductId(), quantity)) {
                throw new IllegalArgumentException("Insufficient stock for product: " + item.getProduct().getProductId());
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);  // ✅ Explicitly save
        }

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public CartDTO removeFromCart(String userId, String cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found: " + cartItemId));

        cart.removeCartItem(item);
        cartItemRepository.delete(item);  // ✅ Explicitly delete
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    /**
     * Clear entire cart
     */
    @Transactional
    public void clearCart(String userId) {
        System.out.println("🧹 Clearing cart for user: " + userId);
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> {
                    // ✅ Delete all cart items first
                    cartItemRepository.deleteAll(cart.getCartItems());
                    cart.getCartItems().clear();
                    cartRepository.save(cart);
                    System.out.println("✅ Cart cleared");
                });
    }

    /**
     * Get cart total amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(String userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getCartItems().stream()
                        .map(item -> item.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Get cart item count
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(String userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getCartItems().size())
                .orElse(0);
    }

    /**
     * Convert Cart entity to DTO
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getCartItems().stream()
                .map(item -> {
                    Product product = item.getProduct();
                    BigDecimal subtotal = product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemDTO(
                            item.getCartItemId(),
                            product.getProductId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(
                cart.getCartId(),
                cart.getUserId(),
                items,
                total,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}