package com.example.loginpage.service.impl;

import com.example.loginpage.dto.CouponDTO;
import com.example.loginpage.model.Coupon;
import com.example.loginpage.repository.ICouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponService {

    private final ICouponRepository couponRepository;

    @Autowired
    public CouponService(ICouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponDTO validateCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            throw new IllegalArgumentException("Coupon has expired or is not yet active");
        }

        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("Coupon usage limit has been reached");
        }

        if (orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new IllegalArgumentException("Minimum order amount of R" +
                    coupon.getMinimumOrderAmount() + " required");
        }

        return convertToDTO(coupon);
    }

    @Transactional
    public CouponDTO applyCoupon(String orderId, String couponCode) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        Coupon savedCoupon = couponRepository.save(coupon);

        return convertToDTO(savedCoupon);
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        return new CouponDTO(
                coupon.getCouponId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinimumOrderAmount(),
                coupon.getMaxDiscountAmount(),
                coupon.getValidFrom(),
                coupon.getValidTo(),
                coupon.getUsageLimit(),
                coupon.getUsageCount(),
                coupon.getIsActive()
        );
    }
}