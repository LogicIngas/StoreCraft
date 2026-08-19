package com.example.loginpage.factory;

import com.example.loginpage.model.Review;
import com.example.loginpage.util.Helper;

public class ReviewFactory {

    public static Review createReview(
            String productId,
            String userId,
            Integer rating,
            String reviewText) {

        if (Helper.isNullOrEmpty(productId) || Helper.isNullOrEmpty(userId)) {
            return null;
        }
        if (rating == null || rating < 1 || rating > 5) {
            return null;
        }

        return new Review(productId, userId, rating, reviewText);
    }
}