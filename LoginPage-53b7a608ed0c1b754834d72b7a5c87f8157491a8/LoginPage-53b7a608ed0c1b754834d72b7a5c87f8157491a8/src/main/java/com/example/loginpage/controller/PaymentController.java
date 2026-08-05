package com.example.loginpage.controller;

import com.example.loginpage.dto.*;
import com.example.loginpage.service.impl.OrderService;
import com.example.loginpage.service.impl.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    /**
     * Process payment and create order
     * ✅ FIXED: Proper error handling and order status update
     * POST /payment/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> processCheckout(@RequestBody PaymentCheckoutRequest request) {
        try {
            System.out.println("🛒 Checkout request received for user: " + request.userId());

            // Validate request
            if (request.userId() == null || request.userId().trim().isEmpty()) {
                System.err.println("❌ User ID is required");
                return ResponseEntity.badRequest().body(
                        new PaymentErrorResponse("User ID is required"));
            }
            if (request.shippingAddress() == null || request.shippingAddress().trim().isEmpty()) {
                System.err.println("❌ Shipping address is required");
                return ResponseEntity.badRequest().body(
                        new PaymentErrorResponse("Shipping address is required"));
            }
            if (request.cardToken() == null || request.cardToken().trim().isEmpty()) {
                System.err.println("❌ Payment token is required");
                return ResponseEntity.badRequest().body(
                        new PaymentErrorResponse("Payment token is required"));
            }

            // ✅ Process payment (which now creates order internally)
            PaymentDTO payment = paymentService.processCheckout(
                    request.userId(),
                    request.shippingAddress(),
                    request.cardToken(),
                    request.cardLast4(),
                    request.cardHolderName()
            );

            System.out.println("✅ Payment successful: " + payment.paymentId());
            System.out.println("✅ Order created: " + payment.orderId());

            // ✅ Update order status from PENDING to CONFIRMED after successful payment
            OrderDTO updatedOrder = orderService.updateOrderStatus(payment.orderId(), "CONFIRMED");
            System.out.println("✅ Order status updated to CONFIRMED: " + updatedOrder.orderId());

            // Build success response
            PaymentCheckoutResponse response = new PaymentCheckoutResponse(
                    true,
                    payment.paymentId(),
                    payment.transactionId(),
                    "Payment processed successfully! Order confirmed.",
                    payment
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new PaymentErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("❌ Payment failed: " + e.getMessage());
            return ResponseEntity.status(402).body(
                    new PaymentErrorResponse("Payment processing failed: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                    new PaymentErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Get payment by ID
     * GET /payment/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId) {
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    /**
     * Get payment by order ID
     * GET /payment/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }

    /**
     * Get all payments for a user
     * GET /payment/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable String userId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get payments by status
     * GET /payment/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get user's payments by status
     * GET /payment/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getPaymentsByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable String status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get total amount paid by user
     * GET /payment/user/{userId}/total
     */
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<?> getTotalAmountPaidByUser(@PathVariable String userId) {
        java.math.BigDecimal total = paymentService.getTotalAmountPaidByUser(userId);
        return ResponseEntity.ok(new PaymentTotalPaidResponse(total));
    }

    /**
     * Get completed payment count for user
     * GET /payment/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getCompletedPaymentCount(@PathVariable String userId) {
        long count = paymentService.getCompletedPaymentCount(userId);
        return ResponseEntity.ok(new PaymentCountResponse(count));
    }

    /**
     * Verify payment status
     * GET /payment/verify/{paymentId}
     */
    @GetMapping("/verify/{paymentId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String paymentId) {
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        PaymentVerifyResponse response = new PaymentVerifyResponse(
                payment.status().equals("COMPLETED"),
                paymentId,
                payment.status().equals("COMPLETED") ? "Payment verified successfully" :
                        "Payment status: " + payment.status()
        );
        return ResponseEntity.ok(response);
    }
}