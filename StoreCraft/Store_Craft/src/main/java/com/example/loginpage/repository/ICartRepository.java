package com.example.loginpage.repository;

import com.example.loginpage.model.Cart;
import com.example.loginpage.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {

    /**
     * Find cart by user ID
     */
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") String userId);

    /**
     * Check if user has a cart
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cart c WHERE c.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);
}
