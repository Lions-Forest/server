package com.example.lionsforest.domain.review.repository;

import com.example.lionsforest.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByGroupIdAndUserId(Long groupId, Long userId);

    List<Review> findByGroupId(Long groupId);
    List<Review> findByUserId(Long userId);
}
