package com.example.loginpage.repository;

import com.example.loginpage.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWishlistRepository extends JpaRepository<Wishlist, String> {

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId ORDER BY w.createdAt DESC")
    List<Wishlist> findByUserId(@Param("userId") String userId);

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId AND w.productId = :productId")
    Optional<Wishlist> findByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.userId = :userId AND w.productId = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.userId = :userId AND w.productId = :productId")
    int deleteByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);
}