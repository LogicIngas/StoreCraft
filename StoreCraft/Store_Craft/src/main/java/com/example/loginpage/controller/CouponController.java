package com.example.loginpage.controller;

import com.example.loginpage.model.Coupon;
import com.example.loginpage.service.impl.BrevoEmailService;
import com.example.loginpage.service.impl.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService service;
    private final BrevoEmailService brevoEmailService;

    @Autowired
    public CouponController(CouponService service, @Autowired(required = false) BrevoEmailService brevoEmailService) {
        this.service = service;
        this.brevoEmailService = brevoEmailService;
    }

    @PostMapping("/validate")
    public Coupon validateCoupon(@RequestBody ValidateRequest request) {
        return service.validateCoupon(request.code, request.orderAmount);
    }

    @PostMapping("/apply/{orderId}")
    public Coupon applyCoupon(@PathVariable String orderId, @RequestBody ApplyRequest request) {
        return service.applyCoupon(orderId, request.couponCode);
    }

    @PostMapping("/send-claim-email")
    public ResponseEntity<?> sendClaimEmail(@RequestBody SendClaimEmailRequest request) {
        if (request == null || request.email == null || request.email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        String email = request.email.trim().toLowerCase();
        String productName = (request.productName != null && !request.productName.isBlank())
                ? request.productName
                : "your selected product";
        String couponCode = (request.couponCode != null && !request.couponCode.isBlank())
                ? request.couponCode
                : "SAVE-10";

        if (brevoEmailService != null) {
            String subject = "🏷️ Your 10% Coupon for " + productName + " [" + couponCode + "]";
            String htmlContent = "<div style=\"font-family:Segoe UI,Roboto,sans-serif;max-width:580px;margin:20px auto;padding:32px;border:1px solid #e2e8f0;border-radius:16px;background:#ffffff;box-shadow:0 4px 12px rgba(0,0,0,0.05);\">"
                    + "<div style=\"text-align:center;margin-bottom:24px;\">"
                    + "<span style=\"font-size:40px;\">🏷️</span>"
                    + "<h1 style=\"color:#0f766e;font-size:24px;margin:8px 0;\">Here is Your 10% Discount Coupon!</h1>"
                    + "<p style=\"color:#64748b;font-size:15px;margin:0;\">Thank you for shopping with StoreCraft.</p>"
                    + "</div>"
                    + "<div style=\"background:#f8fafc;border-radius:12px;padding:20px;margin:20px 0;border:1px solid #e2e8f0;\">"
                    + "<div style=\"font-size:14px;color:#64748b;margin-bottom:4px;\">Product Discount:</div>"
                    + "<div style=\"font-size:18px;font-weight:600;color:#0f172a;\">" + productName + "</div>"
                    + "</div>"
                    + "<div style=\"background:#f0fdf4;border:2px dashed #059669;padding:20px;text-align:center;border-radius:12px;margin:24px 0;\">"
                    + "<div style=\"font-size:12px;color:#047857;font-weight:700;letter-spacing:1px;text-transform:uppercase;margin-bottom:6px;\">Your Coupon Code</div>"
                    + "<div style=\"font-size:32px;font-weight:800;letter-spacing:6px;color:#059669;\">" + couponCode + "</div>"
                    + "<div style=\"font-size:13px;color:#16a34a;margin-top:6px;\">10% OFF • Single-Use Only</div>"
                    + "</div>"
                    + "<p style=\"color:#475569;font-size:14px;line-height:1.6;\">"
                    + "Present this code at physical store checkout or mention it upon delivery. This code is valid for <strong>30 days</strong>."
                    + "</p>"
                    + "<hr style=\"border:none;border-top:1px solid #e2e8f0;margin:24px 0;\" />"
                    + "<p style=\"color:#94a3b8;font-size:12px;text-align:center;margin:0;\">"
                    + "StoreCraft E-Marketplace • If you did not request this coupon, please ignore this email."
                    + "</p>"
                    + "</div>";

            try {
                brevoEmailService.send(email, "Customer", subject, htmlContent);
                System.out.println("✅ Coupon email successfully sent via Brevo to: " + email);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to dispatch coupon email: " + e.getMessage());
            }
        } else {
            System.err.println("⚠️ BrevoEmailService is not available");
        }

        return ResponseEntity.ok(Map.of("ok", true, "code", couponCode));
    }

    public static class ValidateRequest {
        public String code;
        public BigDecimal orderAmount;
    }

    public static class ApplyRequest {
        public String couponCode;
    }

    public static class SendClaimEmailRequest {
        public String email;
        public String couponCode;
        public String productName;
    }
}