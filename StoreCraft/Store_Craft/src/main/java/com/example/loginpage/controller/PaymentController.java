package com.example.loginpage.controller;

import com.example.loginpage.model.Payment;
import com.example.loginpage.service.impl.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public Payment checkout(@RequestBody CheckoutRequest request) {
        return service.processCheckout(
                request.userId,
                request.shippingAddress,
                request.cardToken,
                request.cardLast4,
                request.cardHolderName
        );
    }

    @GetMapping("/{paymentId}")
    public Payment getPayment(@PathVariable String paymentId) {
        return service.getPaymentById(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrder(@PathVariable String orderId) {
        return service.getPaymentByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<Payment> getPaymentsByUser(@PathVariable String userId) {
        return service.getPaymentsByUserId(userId);
    }

    @GetMapping("/status/{status}")
    public List<Payment> getPaymentsByStatus(@PathVariable String status) {
        return service.getPaymentsByStatus(status);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Payment> getPaymentsByUserAndStatus(@PathVariable String userId, @PathVariable String status) {
        return service.getPaymentsByUserIdAndStatus(userId, status);
    }

    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalPaid(@PathVariable String userId) {
        return service.getTotalAmountPaidByUser(userId);
    }

    @GetMapping("/verify/{paymentId}")
    public boolean verifyPayment(@PathVariable String paymentId) {
        return service.verifyPayment(paymentId);
    }

    public static class CheckoutRequest {
        public String userId;
        public String shippingAddress;
        public String cardToken;
        public String cardLast4;
        public String cardHolderName;
    }
}