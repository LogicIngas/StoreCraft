package com.example.loginpage.service.impl;

import com.example.loginpage.dto.ReviewDTO;
import com.example.loginpage.model.Review;
import com.example.loginpage.model.User;
import com.example.loginpage.repository.IReviewRepository;
import com.example.loginpage.repository.IProductRepository;
import com.example.loginpage.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final IReviewRepository reviewRepository;
    private final IProductRepository productRepository;
    private final IUserRepository userRepository;

    @Autowired
    public ReviewService(IReviewRepository reviewRepository, IProductRepository productRepository, IUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewDTO createReview(String productId, String userId, Integer rating, String reviewText) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        Review review = new Review(productId, userId, rating, reviewText);
        Review savedReview = reviewRepository.save(review);

        User user = userRepository.findById(userId).orElse(null);
        String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Anonymous";

        return new ReviewDTO(
                savedReview.getReviewId(),
                savedReview.getProductId(),
                savedReview.getUserId(),
                userName,
                savedReview.getRating(),
                savedReview.getReviewText(),
                savedReview.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(review -> {
                    User user = userRepository.findById(review.getUserId()).orElse(null);
                    String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Anonymous";
                    return new ReviewDTO(
                            review.getReviewId(),
                            review.getProductId(),
                            review.getUserId(),
                            userName,
                            review.getRating(),
                            review.getReviewText(),
                            review.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(String productId) {
        return reviewRepository.getAverageRatingByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long getReviewCount(String productId) {
        return reviewRepository.countByProductId(productId);
    }
}