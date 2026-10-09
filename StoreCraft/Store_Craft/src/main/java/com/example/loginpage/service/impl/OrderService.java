package com.example.loginpage.service.impl;

import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import com.example.loginpage.model.Order;
import com.example.loginpage.model.OrderItem;
import com.example.loginpage.repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final IOrderRepository orderRepository;
    private final CartService cartService;
    private final com.example.loginpage.service.impl.UserService userService;
    private final com.example.loginpage.service.impl.EmailService emailService;

    @Autowired
    public OrderService(IOrderRepository orderRepository, CartService cartService,
                        com.example.loginpage.service.impl.UserService userService,
                        com.example.loginpage.service.impl.EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Transactional
    public Order createOrderFromCart(String userId, String shippingAddress) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create order.");
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(userId, totalAmount, shippingAddress);
        order.setCreatedAt(LocalDateTime.now());

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
            );
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserIdAndStatus(String userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    @Transactional
    public Order updateOrderStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        // Notify user if status is changed to something meaningful
        if (newStatus.equalsIgnoreCase("SHIPPED") || newStatus.equalsIgnoreCase("DELIVERED") || newStatus.equalsIgnoreCase("CANCELLED")) {
            try {
                com.example.loginpage.model.User user = userService.read(order.getUserId());
                if (user != null) {
                    String name = user.getFirstName() != null ? user.getFirstName() : "Customer";
                    if (newStatus.equalsIgnoreCase("SHIPPED")) {
                        emailService.sendShippingNotification(user.getEmail(), name, order.getOrderId(), "TBA", "3-5 business days");
                    } else {
                        // For delivered/cancelled, maybe send a generic one or build one
                        // but shipping is the primary one mentioned.
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to send status update email: " + ex.getMessage());
            }
        }
        return updated;
    }

    @Transactional(readOnly = true)
    public Order getOrderSummary(String orderId) {
        return getOrderById(orderId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersBySellerId(String sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId);
    }
}