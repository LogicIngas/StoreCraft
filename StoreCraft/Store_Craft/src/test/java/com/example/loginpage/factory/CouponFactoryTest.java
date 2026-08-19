package com.example.loginpage.factory;

import com.example.loginpage.model.Coupon;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CouponFactoryTest {

    @Test
    public void testCreateCoupon_Success() {
        String userId = Helper.generateShortUUID(); // Declared as requested
        Coupon coupon = CouponFactory.createCoupon("SAVE20", "20% off", "PERCENTAGE", new BigDecimal("20.00"), new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        assertNotNull(coupon);
    }

    @Test
    public void testCreateCoupon_Fail_InvalidCode() {
        String userId = Helper.generateShortUUID();
        Coupon coupon = CouponFactory.createCoupon("", "20% off", "PERCENTAGE", new BigDecimal("20.00"), new BigDecimal("100.00"), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        assertNull(coupon);
    }
}
