package com.example.loginpage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponDTO(
        String couponId,
        String code,
        String description,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minimumOrderAmount,
        BigDecimal maxDiscountAmount,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Integer usageLimit,
        Integer usageCount,
        Boolean isActive
) {}