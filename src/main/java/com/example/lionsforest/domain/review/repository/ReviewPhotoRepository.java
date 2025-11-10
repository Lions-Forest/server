package com.example.lionsforest.domain.review.repository;

import com.example.lionsforest.domain.review.Review;
import com.example.lionsforest.domain.review.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {
    List<ReviewPhoto> findAllByReview(Review review);
    int countByReviewId(Long reviewId);
}
