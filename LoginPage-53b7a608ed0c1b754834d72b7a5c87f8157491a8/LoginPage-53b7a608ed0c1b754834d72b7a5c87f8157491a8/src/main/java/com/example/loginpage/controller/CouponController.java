package com.example.loginpage.controller;

import com.example.loginpage.dto.CouponDTO;
import com.example.loginpage.dto.ErrorResponse;
import com.example.loginpage.service.impl.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupon")
@CrossOrigin(origins = "http://localhost:5173")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody ValidateCouponRequest request) {
        try {
            CouponDTO coupon = couponService.validateCoupon(request.code(), request.orderAmount());
            return ResponseEntity.ok(coupon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/apply/{orderId}")
    public ResponseEntity<?> applyCoupon(@PathVariable String orderId, @RequestBody ApplyCouponRequest request) {
        try {
            CouponDTO applied = couponService.applyCoupon(orderId, request.couponCode());
            return ResponseEntity.ok(applied);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public record ValidateCouponRequest(String code, BigDecimal orderAmount) {}
    public record ApplyCouponRequest(String couponCode) {}
}