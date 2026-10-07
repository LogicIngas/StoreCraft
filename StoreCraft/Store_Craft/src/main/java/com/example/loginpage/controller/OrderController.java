package com.example.loginpage.controller;

import com.example.loginpage.dto.OrderDTO;
import com.example.loginpage.dto.OrderItemDTO;
import com.example.loginpage.model.Order;
import com.example.loginpage.model.OrderItem;
import com.example.loginpage.service.impl.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public OrderDTO createOrder(@RequestBody CreateOrderRequest request) {
        Order order = service.createOrderFromCart(request.userId, request.shippingAddress);
        return convertToOrderDTO(order);
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable String orderId) {
        Order order = service.getOrderById(orderId);
        return convertToOrderDTO(order);
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable String userId) {
        List<Order> orders = service.getOrdersByUserId(userId);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    public List<OrderDTO> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = service.getOrdersByStatus(status);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<OrderDTO> getOrdersByUserAndStatus(@PathVariable String userId,
                                                   @PathVariable String status) {
        List<Order> orders = service.getOrdersByUserIdAndStatus(userId, status);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{orderId}/status")
    public OrderDTO updateOrderStatus(@PathVariable String orderId, @RequestBody StatusRequest request) {
        Order order = service.updateOrderStatus(orderId, request.status);
        return convertToOrderDTO(order);
    }

    @GetMapping("/summary/{orderId}")
    public OrderDTO getOrderSummary(@PathVariable String orderId) {
        Order order = service.getOrderSummary(orderId);
        return convertToOrderDTO(order);
    }

    // ---------- Conversion Helper ----------
    private OrderDTO convertToOrderDTO(Order order) {
        if (order == null) return null;
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getOrderItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getOrderId(),
                order.getUserId(),
                itemDTOs,
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    // ---------- Request DTOs ----------
    public static class CreateOrderRequest {
        public String userId;
        public String shippingAddress;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    }

    public static class StatusRequest {
        public String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}