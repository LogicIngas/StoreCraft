package com.example.loginpage.controller;

import com.example.loginpage.dto.ErrorResponse;
import com.example.loginpage.dto.ReviewDTO;
import com.example.loginpage.service.impl.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request) {
        try {
            ReviewDTO review = reviewService.createReview(
                    request.productId(),
                    request.userId(),
                    request.rating(),
                    request.reviewText()
            );
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable String productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<?> getAverageRating(@PathVariable String productId) {
        Double average = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<?> getReviewCount(@PathVariable String productId) {
        long count = reviewService.getReviewCount(productId);
        return ResponseEntity.ok(count);
    }

    public record CreateReviewRequest(String productId, String userId, Integer rating, String reviewText) {}
}