package com.example.loginpage.controller;

import com.example.loginpage.dto.CreateOrderRequest;
import com.example.loginpage.dto.OrderDTO;
import com.example.loginpage.dto.UpdateOrderStatusRequest;
import com.example.loginpage.service.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create order from cart
     * POST /order/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderFromCartRequest request) {
        try {
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("User ID is required");
            }
            if (request.shippingAddress() == null || request.shippingAddress().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Shipping address is required");
            }

            OrderDTO order = orderService.createOrderFromCart(request.userId(), request.shippingAddress());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get order by ID
     * GET /order/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders for a user
     * GET /order/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status
     * GET /order/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Status is required");
        }
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get user's orders by status
     * GET /order/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getOrdersByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable String status) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Status is required");
        }
        List<OrderDTO> orders = orderService.getOrdersByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status
     * PUT /order/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            if (request.status() == null || request.status().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Status is required");
            }

            // Validate status value
            String status = request.status().toUpperCase();
            if (!isValidStatus(status)) {
                return ResponseEntity.badRequest().body(
                        "Invalid status. Must be one of: PENDING, CONFIRMED, SHIPPED, DELIVERED"
                );
            }

            OrderDTO order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get order summary (for success page)
     * GET /order/summary/{orderId}
     */
    @GetMapping("/summary/{orderId}")
    public ResponseEntity<?> getOrderSummary(@PathVariable String orderId) {
        OrderDTO order = orderService.getOrderSummary(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    /**
     * Validate order status
     */
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") ||
                status.equals("CONFIRMED") ||
                status.equals("SHIPPED") ||
                status.equals("DELIVERED");
    }

    // Request DTOs
    public record CreateOrderFromCartRequest(
            String userId,
            String shippingAddress
    ) {}
}