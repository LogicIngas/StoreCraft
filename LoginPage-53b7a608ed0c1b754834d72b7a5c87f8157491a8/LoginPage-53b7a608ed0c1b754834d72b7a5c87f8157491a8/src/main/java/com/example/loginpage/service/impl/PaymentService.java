package com.example.loginpage.service.impl;

import com.example.loginpage.model.Payment;
import com.example.loginpage.repository.IPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final IPaymentRepository paymentRepository;
    private final CartService cartService;
    private final OrderService orderService;

    @Autowired
    public PaymentService(IPaymentRepository paymentRepository, CartService cartService,
                          OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @Transactional
    public Payment processCheckout(String userId, String shippingAddress, String cardToken,
                                   String cardLast4, String cardHolderName) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("User ID is required");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new RuntimeException("Shipping address is required");
        }
        if (cardToken == null || cardToken.trim().isEmpty()) {
            throw new RuntimeException("Payment token is required");
        }

        com.example.loginpage.model.Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot process payment.");
        }

        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        com.example.loginpage.model.Order order = orderService.createOrderFromCart(userId, shippingAddress);

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(total);
        payment.setCurrency("ZAR");
        payment.setPaymentMethod("MOCK");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("PROCESSING");
        payment.setCardLast4(cardLast4 != null && !cardLast4.isEmpty() ? cardLast4 : "****");
        payment.setCardBrand("MOCK");
        payment.setCardHolderName(cardHolderName != null && !cardHolderName.isEmpty() ? cardHolderName : "Mock User");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        try {
            savedPayment.setStatus("COMPLETED");
            savedPayment.setGatewayResponse("Mock payment gateway: Approved");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            Payment completedPayment = paymentRepository.save(savedPayment);

            orderService.updateOrderStatus(order.getOrderId(), "CONFIRMED");
            cartService.clearCart(userId);

            return completedPayment;

        } catch (Exception e) {
            savedPayment.setStatus("FAILED");
            savedPayment.setErrorMessage(e.getMessage());
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserIdAndStatus(String userId, String status) {
        return paymentRepository.findByUserIdAndStatus(userId, status);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountPaidByUser(String userId) {
        return paymentRepository.getTotalAmountPaidByUser(userId);
    }

    @Transactional(readOnly = true)
    public boolean verifyPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        return payment != null && "COMPLETED".equals(payment.getStatus());
    }
}