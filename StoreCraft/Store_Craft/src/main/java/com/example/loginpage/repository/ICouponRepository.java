package com.example.loginpage.repository;

import com.example.loginpage.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICouponRepository extends JpaRepository<Coupon, String> {

    @Query("SELECT c FROM Coupon c WHERE LOWER(c.code) = LOWER(:code)")
    Optional<Coupon> findByCode(@Param("code") String code);

    @Query("SELECT c FROM Coupon c WHERE LOWER(c.code) = LOWER(:code) AND c.isActive = true")
    Optional<Coupon> findByCodeAndIsActiveTrue(@Param("code") String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.validFrom <= CURRENT_TIMESTAMP AND c.validTo >= CURRENT_TIMESTAMP")
    List<Coupon> findAllActiveCoupons();

    @Query("SELECT c FROM Coupon c WHERE c.discountType = :discountType AND c.isActive = true")
    List<Coupon> findByDiscountType(@Param("discountType") String discountType);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Coupon c WHERE LOWER(c.code) = LOWER(:code)")
    boolean existsByCode(@Param("code") String code);
}