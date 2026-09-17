package com.example.loginpage.controller;

import com.example.loginpage.model.Coupon;
import com.example.loginpage.service.impl.CouponService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupon")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    @PostMapping("/validate")
    public Coupon validateCoupon(@RequestBody ValidateRequest request) {
        return service.validateCoupon(request.code, request.orderAmount);
    }

    @PostMapping("/apply/{orderId}")
    public Coupon applyCoupon(@PathVariable String orderId, @RequestBody ApplyRequest request) {
        return service.applyCoupon(orderId, request.couponCode);
    }

    public static class ValidateRequest {
        public String code;
        public BigDecimal orderAmount;
    }

    public static class ApplyRequest {
        public String couponCode;
    }
}