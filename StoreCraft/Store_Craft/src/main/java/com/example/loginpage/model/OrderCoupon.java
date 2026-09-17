// package com.example.loginpage.model;

// import jakarta.persistence.*;
// import org.hibernate.annotations.UuidGenerator;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "order_coupons")
// public class OrderCoupon {

// @Id
// @UuidGenerator
// @Column(name = "order_coupon_id", updatable = false, nullable = false)
// private String orderCouponId;

// @Column(name = "order_id", nullable = false)
// private String orderId;

// @ManyToOne(fetch = FetchType.EAGER)
// @JoinColumn(name = "coupon_id", nullable = false)
// private Coupon coupon;

// @Column(name = "discount_amount", nullable = false)
// private BigDecimal discountAmount;

// @Column(name = "applied_at", nullable = false, updatable = false)
// private LocalDateTime appliedAt;

// public OrderCoupon() {}

// public OrderCoupon(String orderId, Coupon coupon, BigDecimal discountAmount)
// {
// this.orderId = orderId;
// this.coupon = coupon;
// this.discountAmount = discountAmount;
// this.appliedAt = LocalDateTime.now();
// }

// public String getOrderCouponId() { return orderCouponId; }
// public void setOrderCouponId(String orderCouponId) { this.orderCouponId =
// orderCouponId; }
// public String getOrderId() { return orderId; }
// public void setOrderId(String orderId) { this.orderId = orderId; }
// public Coupon getCoupon() { return coupon; }
// public void setCoupon(Coupon coupon) { this.coupon = coupon; }
// public BigDecimal getDiscountAmount() { return discountAmount; }
// public void setDiscountAmount(BigDecimal discountAmount) {
// this.discountAmount = discountAmount; }
// public LocalDateTime getAppliedAt() { return appliedAt; }
// public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt =
// appliedAt; }
// }