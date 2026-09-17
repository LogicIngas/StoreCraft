package com.example.loginpage.controller;

import com.example.loginpage.model.Order;
import com.example.loginpage.model.User;
import com.example.loginpage.repository.*;
import com.example.loginpage.service.impl.OrderService;
import com.example.loginpage.security.RoleRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
@RoleRequired("ADMIN")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Enforced by Spring Security
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IPaymentRepository paymentRepository;

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        long totalProducts = productRepository.count();
        long availableProducts = productRepository.findAvailableProducts().size();
        stats.put("totalProducts", totalProducts);
        stats.put("availableProducts", availableProducts);

        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long confirmedOrders = allOrders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
        long shippedOrders = allOrders.stream().filter(o -> "SHIPPED".equals(o.getStatus())).count();
        long deliveredOrders = allOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("confirmedOrders", confirmedOrders);
        stats.put("shippedOrders", shippedOrders);
        stats.put("deliveredOrders", deliveredOrders);

        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        List<Map<String, Object>> recentOrders = allOrders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("orderId", order.getOrderId());
                    orderMap.put("userId", order.getUserId());
                    orderMap.put("totalAmount", order.getTotalAmount());
                    orderMap.put("status", order.getStatus());
                    orderMap.put("createdAt", order.getCreatedAt());
                    return orderMap;
                })
                .collect(Collectors.toList());
        stats.put("recentOrders", recentOrders);

        return stats;
    }

    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/orders/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        return orderRepository.findByStatus(status);
    }

    @PutMapping("/orders/{orderId}/status")
    public Order updateOrderStatus(@PathVariable String orderId, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        return orderService.updateOrderStatus(orderId, newStatus);
    }

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/top")
    public List<Map<String, Object>> getTopUsers() {
        return userRepository.findAll().stream()
                .limit(5)
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", user.getUserId());
                    userMap.put("email", user.getEmail());
                    userMap.put("firstName", user.getFirstName());
                    userMap.put("lastName", user.getLastName());
                    userMap.put("role", user.getRole().getName());
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/orders/today")
    public List<Order> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt().isAfter(startOfDay))
                .collect(Collectors.toList());
    }

    @GetMapping("/revenue/monthly")
    public Map<String, BigDecimal> getMonthlyRevenue() {
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 12; i++) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = now.minusMonths(i).withDayOfMonth(now.minusMonths(i).toLocalDate().lengthOfMonth())
                    .withHour(23).withMinute(59).withSecond(59);

            BigDecimal revenue = orderRepository.findAll().stream()
                    .filter(order -> order.getCreatedAt().isAfter(monthStart) && order.getCreatedAt().isBefore(monthEnd))
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String monthName = monthStart.getMonth().toString() + " " + monthStart.getYear();
            monthlyRevenue.put(monthName, revenue);
        }

        return monthlyRevenue;
    }
}