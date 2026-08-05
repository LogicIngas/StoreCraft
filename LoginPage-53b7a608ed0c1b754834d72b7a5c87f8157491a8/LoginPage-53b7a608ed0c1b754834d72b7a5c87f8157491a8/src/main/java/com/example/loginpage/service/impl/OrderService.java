package com.example.loginpage.service.impl;

import com.example.loginpage.dto.OrderDTO;
import com.example.loginpage.dto.OrderItemDTO;
import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Order;
import com.example.loginpage.model.OrderItem;
import com.example.loginpage.repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final IOrderRepository orderRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(IOrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    /**
     * ✅ FIXED: Create order from cart WITHOUT clearing cart
     * Cart clearing is now handled by PaymentService after successful payment
     */
    @Transactional
    public OrderDTO createOrderFromCart(String userId, String shippingAddress) {
        System.out.println("📋 Creating order for user: " + userId);

        // Get user's cart
        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot create order.");
        }

        System.out.println("📦 Cart contains " + cart.getCartItems().size() + " items");

        // Calculate total amount
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("💰 Order total: R " + totalAmount);

        // Create order
        Order order = new Order(userId, totalAmount, shippingAddress);

        // Add order items from cart items
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            order.addOrderItem(orderItem);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Order created with ID: " + savedOrder.getOrderId());

        // ✅ IMPORTANT: Do NOT clear cart here. PaymentService will do it after successful payment.
        // This prevents orphaned orders if cart clearing fails.

        return convertToDTO(savedOrder);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get all orders for a user
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserIdAndStatus(String userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderDTO updateOrderStatus(String orderId, String newStatus) {
        System.out.println("📝 Updating order " + orderId + " status to: " + newStatus);
        return orderRepository.findById(orderId)
                .map(order -> {
                    String oldStatus = order.getStatus();
                    order.setStatus(newStatus);
                    Order updated = orderRepository.save(order);
                    System.out.println("✅ Order status updated from " + oldStatus + " to " + newStatus);
                    return convertToDTO(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    /**
     * Get order summary (for success page)
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderSummary(String orderId) {
        return getOrderById(orderId);
    }

    /**
     * Convert Order entity to DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getOrderItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getOrderId(),
                order.getUserId(),
                items,
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}