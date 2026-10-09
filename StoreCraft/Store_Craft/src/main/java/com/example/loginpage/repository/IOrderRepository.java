package com.example.loginpage.repository;

import com.example.loginpage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {

    /**
     * Find all orders for a specific user
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") String userId);

    /**
     * Find orders by status
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByStatus(@Param("status") String status);

    /**
     * Find user's orders by status
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    /**
     * A4 — Find orders containing products sold by a specific seller
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems i WHERE i.product.sellerId = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") String sellerId);
}