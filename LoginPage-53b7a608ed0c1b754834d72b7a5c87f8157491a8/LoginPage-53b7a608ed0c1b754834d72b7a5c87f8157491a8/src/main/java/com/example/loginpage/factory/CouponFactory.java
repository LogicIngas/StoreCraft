package com.example.loginpage.factory;

import com.example.loginpage.model.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponFactory {

    public static Coupon createCoupon(
            String code,
            String description,
            String discountType,
            BigDecimal discountValue,
            BigDecimal minimumOrderAmount,
            LocalDateTime validFrom,
            LocalDateTime validTo) {

        if (code.trim().isEmpty() || discountValue.compareTo(BigDecimal.ZERO) <= 0 || validFrom == null || validTo == null || validFrom.isAfter(validTo)) {
            return null;
        }

        return new Coupon(
                code,
                description,
                discountType != null ? discountType : "PERCENTAGE",
                discountValue,
                minimumOrderAmount != null ? minimumOrderAmount : BigDecimal.ZERO,
                validFrom,
                validTo
        );
    }
}