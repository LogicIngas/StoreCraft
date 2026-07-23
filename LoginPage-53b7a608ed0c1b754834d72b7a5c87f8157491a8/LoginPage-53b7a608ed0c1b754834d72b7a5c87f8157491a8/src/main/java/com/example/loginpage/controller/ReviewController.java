package com.example.loginpage.controller;

import com.example.loginpage.model.Review;
import com.example.loginpage.service.impl.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Review createReview(@RequestBody CreateRequest request) {
        return service.createReview(request.productId, request.userId, request.rating, request.reviewText);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable String productId) {
        return service.getReviewsByProductId(productId);
    }

    @GetMapping("/product/{productId}/average")
    public Double getAverageRating(@PathVariable String productId) {
        return service.getAverageRating(productId);
    }

    @GetMapping("/product/{productId}/count")
    public long getReviewCount(@PathVariable String productId) {
        return service.getReviewCount(productId);
    }

    // ========== Inner DTOs ==========

    public static class CreateRequest {
        public String productId;
        public String userId;
        public Integer rating;
        public String reviewText;
    }
}