package com.example.loginpage.repository;

import com.example.loginpage.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IPaymentRepository - Data access layer for Payment entity
 * Handles database operations for payment tracking
 */
@Repository
public interface IPaymentRepository extends JpaRepository<Payment, String> {

    /**
     * Find payment by order ID
     * @param orderId the order ID
     * @return Optional containing the payment if found
     */
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId")
    Optional<Payment> findByOrderId(@Param("orderId") String orderId);

    /**
     * Find all payments for a user
     * @param userId the user ID
     * @return List of payments for the user, ordered by creation date descending
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") String userId);

    /**
     * Find payments by status (e.g., COMPLETED, FAILED)
     * @param status the payment status
     * @return List of payments with the given status, ordered by creation date descending
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByStatus(@Param("status") String status);

    /**
     * Find user's payments by status
     * @param userId the user ID
     * @param status the payment status
     * @return List of payments matching both user and status
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * Find payment by transaction ID (gateway reference)
     * @param transactionId the transaction ID from payment gateway
     * @return Optional containing the payment if found
     */
    @Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
    Optional<Payment> findByTransactionId(@Param("transactionId") String transactionId);

    /**
     * Find payments by card last 4 digits
     * @param cardLast4 the last 4 digits of the card
     * @return List of payments using this card
     */
    @Query("SELECT p FROM Payment p WHERE p.cardLast4 = :cardLast4 ORDER BY p.createdAt DESC")
    List<Payment> findByCardLast4(@Param("cardLast4") String cardLast4);

    /**
     * Find payments by payment method
     * @param paymentMethod the payment method (MOCK, CARD, PAYPAL, etc.)
     * @return List of payments using this method
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod ORDER BY p.createdAt DESC")
    List<Payment> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    /**
     * Count completed payments for a user
     * @param userId the user ID
     * @return Number of completed payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    long countCompletedPayments(@Param("userId") String userId);

    /**
     * Count failed payments for a user
     * @param userId the user ID
     * @return Number of failed payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId AND p.status = 'FAILED'")
    long countFailedPayments(@Param("userId") String userId);

    /**
     * Count pending payments for a user
     * @param userId the user ID
     * @return Number of pending payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId AND p.status = 'PENDING'")
    long countPendingPayments(@Param("userId") String userId);

    /**
     * Get total amount paid by user (completed payments only)
     * @param userId the user ID
     * @return Total amount paid by the user
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    BigDecimal getTotalAmountPaidByUser(@Param("userId") String userId);

    /**
     * Get total amount paid by user for a specific currency
     * @param userId the user ID
     * @param currency the currency code (ZAR, USD, etc.)
     * @return Total amount paid in the specified currency
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED' AND p.currency = :currency")
    BigDecimal getTotalAmountPaidByUserAndCurrency(@Param("userId") String userId, @Param("currency") String currency);

    /**
     * Find payments within a date range
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return List of payments in the date range
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find payments for a user within a date range
     * @param userId the user ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return List of payments for the user in the date range
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findByUserIdAndDateRange(@Param("userId") String userId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


    @Query("SELECT p FROM Payment p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByUserId(@Param("userId") String userId,
                                             org.springframework.data.domain.Pageable pageable);

    /**
     * Find recent payments for a user using native query with LIMIT
     * ✅ Alternative: Native query approach
     */
    @Query(value = "SELECT * FROM payments p WHERE p.user_id = :userId ORDER BY p.created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Payment> findRecentPaymentsByUserIdNative(@Param("userId") String userId,
                                                   @Param("limit") int limit);

    /**
     * Check if a payment exists for an order
     * @param orderId the order ID
     * @return true if payment exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p WHERE p.orderId = :orderId")
    boolean existsByOrderId(@Param("orderId") String orderId);

    /**
     * Get payment statistics for a user
     * @param userId the user ID
     * @return Object array containing [totalPayments, completedPayments, failedPayments, totalAmount]
     */
    @Query("SELECT COUNT(p), " +
            "SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END), " +
            "COALESCE(SUM(CASE WHEN p.status = 'COMPLETED' THEN p.amount ELSE 0 END), 0) " +
            "FROM Payment p WHERE p.userId = :userId")
    List<Object[]> getPaymentStatistics(@Param("userId") String userId);

    /**
     * Find all payments with status PROCESSING (for cleanup jobs)
     * @param timeoutThreshold the timeout threshold
     * @return List of payments stuck in PROCESSING status
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PROCESSING' AND p.updatedAt < :timeoutThreshold")
    List<Payment> findStuckProcessingPayments(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    /**
     * Update payment status by ID (batch update)
     * @param paymentId the payment ID
     * @param newStatus the new status
     * @return number of rows updated
     */
    @Query("UPDATE Payment p SET p.status = :newStatus, p.updatedAt = CURRENT_TIMESTAMP WHERE p.paymentId = :paymentId")
    int updatePaymentStatus(@Param("paymentId") String paymentId, @Param("newStatus") String newStatus);

    /**
     * Count total payments for a user
     * @param userId the user ID
     * @return total number of payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.userId = :userId")
    long countTotalPaymentsByUserId(@Param("userId") String userId);

    /**
     * Get average payment amount for a user
     * @param userId the user ID
     * @return average payment amount
     */
    @Query("SELECT COALESCE(AVG(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    BigDecimal getAveragePaymentAmountByUser(@Param("userId") String userId);
}