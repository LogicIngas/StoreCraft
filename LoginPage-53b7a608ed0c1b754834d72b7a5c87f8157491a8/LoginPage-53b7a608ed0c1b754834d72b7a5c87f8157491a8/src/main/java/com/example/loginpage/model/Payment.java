package com.example.loginpage.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity - Tracks all payment transactions
 * Stores mock and real payment data for auditing
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @UuidGenerator
    @Column(name = "payment_id", updatable = false, nullable = false)
    private String paymentId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency; // ZAR, USD, etc.

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // CARD, MOCK, PAYPAL, BANK_TRANSFER

    @Column(name = "transaction_id", length = 100)
    private String transactionId; // Gateway transaction reference

    @Column(name = "status", length = 50)
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(name = "card_last4", length = 4)
    private String cardLast4; // Last 4 digits (masked)

    @Column(name = "card_brand", length = 50)
    private String cardBrand; // VISA, MASTERCARD, AMEX, MOCK

    @Column(name = "card_holder_name", length = 100)
    private String cardHolderName;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // Error details if payment failed

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // Raw response from payment gateway

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {}

    public Payment(String orderId, String userId, BigDecimal amount, String currency) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = "PENDING";
        this.paymentMethod = "MOCK";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}