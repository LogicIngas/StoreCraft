package com.example.loginpage.service.impl;

import com.example.loginpage.dto.CartDTO;
import com.example.loginpage.dto.OrderDTO;
import com.example.loginpage.dto.PaymentDTO;
import com.example.loginpage.model.Payment;
import com.example.loginpage.repository.IPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final CartService cartService;
    private final OrderService orderService;
    private final IPaymentRepository paymentRepository;

    @Autowired
    public PaymentService(IPaymentRepository paymentRepository, CartService cartService, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    /**
     * ✅ CORRECT FLOW FOR REAL APPLICATION
     * All data is captured: Create Order → Process Payment → Update Statuses
     * No NULL values - everything recorded in database!
     */
    @Transactional
    public PaymentDTO processCheckout(String userId, String shippingAddress, String cardToken,
                                      String cardLast4, String cardHolderName) {
        System.out.println("💳 Starting checkout process for user: " + userId);

        // ✅ STEP 1: Validate all inputs upfront
        validateCheckoutInput(userId, shippingAddress, cardToken);

        // ✅ STEP 2: Get cart and verify it has items
        CartDTO cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.items().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot process payment.");
        }
        System.out.println("📦 Cart verified with " + cart.items().size() + " items, total: R " + cart.total());

        // ✅ STEP 3: CREATE ORDER FIRST (with status PENDING)
        // This captures order data to database immediately
        OrderDTO order = orderService.createOrderFromCart(userId, shippingAddress);
        if (order == null) {
            throw new RuntimeException("Failed to create order");
        }
        System.out.println("✅ Order created (PENDING status): " + order.orderId());

        // ✅ STEP 4: CREATE PAYMENT with order_id (no NULL values!)
        Payment payment = createPaymentRecord(order.orderId(), userId, cart.total(),
                cardToken, cardLast4, cardHolderName);
        Payment savedPayment = paymentRepository.save(payment);
        System.out.println("💾 Payment record created (PROCESSING status): " + savedPayment.getPaymentId());

        // ✅ STEP 5: PROCESS PAYMENT TRANSACTION
        try {
            simulatePaymentProcessing(savedPayment);

            // Update payment status to COMPLETED
            savedPayment.setStatus("COMPLETED");
            savedPayment.setGatewayResponse("Mock payment gateway: Approved");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            Payment completedPayment = paymentRepository.save(savedPayment);
            System.out.println("✅ Payment COMPLETED: " + completedPayment.getPaymentId());

            // ✅ STEP 6: UPDATE ORDER STATUS to CONFIRMED (order now paid)
            orderService.updateOrderStatus(order.orderId(), "CONFIRMED");
            System.out.println("✅ Order status updated to CONFIRMED");

            // ✅ STEP 7: Clear cart (payment and order both successful)
            cartService.clearCart(userId);
            System.out.println("✅ Cart cleared for user: " + userId);

            // ✅ EVERYTHING CAPTURED IN DATABASE!
            return convertToDTO(completedPayment);

        } catch (Exception e) {
            // Payment failed - update payment record to FAILED
            savedPayment.setStatus("FAILED");
            savedPayment.setErrorMessage(e.getMessage());
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            System.err.println("❌ Payment FAILED: " + e.getMessage());

            // Order remains PENDING (can be retried or cancelled later)
            System.out.println("⚠️ Order remains PENDING (payment failed)");

            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * ✅ NEW METHOD: Create payment record with order_id already set
     * No NULL values - all data captured!
     */
    private Payment createPaymentRecord(String orderId, String userId, BigDecimal amount,
                                        String cardToken, String cardLast4, String cardHolderName) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);  // ✅ Set orderId from already-created order
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setCurrency("ZAR");
        payment.setPaymentMethod("MOCK");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("PROCESSING");  // Will be updated to COMPLETED or FAILED
        payment.setCardLast4(cardLast4 != null && !cardLast4.isEmpty() ? cardLast4 : "****");
        payment.setCardBrand("MOCK");
        payment.setCardHolderName(cardHolderName != null && !cardHolderName.isEmpty() ? cardHolderName : "Mock User");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }

    /**
     * ✅ Validate checkout input
     */
    private void validateCheckoutInput(String userId, String shippingAddress, String cardToken) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        if (cardToken == null || cardToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment token is required");
        }
        if (cardToken.trim().length() < 3) {
            throw new IllegalArgumentException("Invalid payment token. Please enter a valid token.");
        }
    }

    /**
     * Simulate payment gateway processing
     */
    private void simulatePaymentProcessing(Payment payment) throws Exception {
        try {
            System.out.println("⏳ Simulating payment gateway processing...");
            // Simulate network delay
            Thread.sleep(1000 + (int)(Math.random() * 1000));
            System.out.println("💳 Mock payment gateway: Processing complete");

            // Simulate occasional gateway failures (10% chance) for testing
            double random = Math.random();
            if (random > 0.95) { // 5% failure rate for testing
                throw new Exception("Mock gateway: Simulated payment failure");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted", e);
        }
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Get all payments for a user
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUserIdAndStatus(String userId, String status) {
        return paymentRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get total amount paid by user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountPaidByUser(String userId) {
        return paymentRepository.getTotalAmountPaidByUser(userId);
    }

    /**
     * Get count of completed payments for user
     */
    @Transactional(readOnly = true)
    public long getCompletedPaymentCount(String userId) {
        return paymentRepository.countCompletedPayments(userId);
    }

    /**
     * Convert Payment entity to DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getStatus(),
                payment.getCardLast4(),
                payment.getCardBrand(),
                payment.getCardHolderName(),
                payment.getErrorMessage(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}