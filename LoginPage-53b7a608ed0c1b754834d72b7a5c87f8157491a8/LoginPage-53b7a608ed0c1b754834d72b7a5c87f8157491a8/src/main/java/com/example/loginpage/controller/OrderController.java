package com.example.loginpage.controller;

import com.example.loginpage.model.Order;
import com.example.loginpage.service.impl.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        return service.createOrderFromCart(request.userId, request.shippingAddress);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return service.getOrderById(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable String userId) {
        return service.getOrdersByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        return service.getOrdersByStatus(status);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Order> getOrdersByUserAndStatus(@PathVariable String userId, @PathVariable String status) {
        return service.getOrdersByUserIdAndStatus(userId, status);
    }

    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable String orderId, @RequestBody StatusRequest request) {
        return service.updateOrderStatus(orderId, request.status);
    }

    @GetMapping("/summary/{orderId}")
    public Order getOrderSummary(@PathVariable String orderId) {
        return service.getOrderSummary(orderId);
    }

    // ========== Inner DTOs ==========

    public static class CreateOrderRequest {
        public String userId;
        public String shippingAddress;
    }

    public static class StatusRequest {
        public String status;
    }
}