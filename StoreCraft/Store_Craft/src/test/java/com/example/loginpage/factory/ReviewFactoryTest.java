package com.example.loginpage.factory;

import com.example.loginpage.model.Review;
import com.example.loginpage.util.Helper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewFactoryTest {

    @Test
    public void testCreateReview_Success() {
        String userId = Helper.generateShortUUID();
        Review review = ReviewFactory.createReview("PROD123", userId, 5, "Great product!");
        assertNotNull(review);
    }

    @Test
    public void testCreateReview_Fail_InvalidRating() {
        String userId = Helper.generateShortUUID();
        Review review = ReviewFactory.createReview("PROD123", userId, 6, "Excellent!");
        assertNull(review);
    }
}
