package com.example.loginpage.service.impl;

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
    public Coupon validateCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            throw new RuntimeException("Coupon has expired or is not yet active");
        }
        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit has been reached");
        }
        if (orderAmount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new RuntimeException("Minimum order amount of R" +
                    coupon.getMinimumOrderAmount() + " required");
        }

        return coupon;
    }

    @Transactional
    public Coupon applyCoupon(String orderId, String couponCode) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        coupon.setUsageCount(coupon.getUsageCount() + 1);
        return couponRepository.save(coupon);
    }
}