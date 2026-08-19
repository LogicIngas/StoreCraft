package com.example.loginpage.repository;

import com.example.loginpage.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, String> {

    @Query("SELECT r FROM Review r WHERE r.productId = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductId(@Param("productId") String productId);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
    List<Review> findByUserId(@Param("userId") String userId);

    @Query("SELECT r FROM Review r WHERE r.rating = :rating ORDER BY r.createdAt DESC")
    List<Review> findByRating(@Param("rating") Integer rating);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") String productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId")
    long countByProductId(@Param("productId") String productId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.userId = :userId AND r.productId = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);
}