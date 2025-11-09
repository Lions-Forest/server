package com.example.lionsforest.domain.review.service;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.review.Review;
import com.example.lionsforest.domain.review.dto.response.ReviewResponseDto;
import com.example.lionsforest.domain.review.repository.ReviewRepository;
import com.example.lionsforest.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // 후기 생성
    @Transactional
    public ReviewResponseDto createReview(Long groupId, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = Review.builder()
                .group(group)
                .user(user)
                .build();

        Review saved = ReviewRepository.save(review);
        return ReviewResponseDto.fromEntity(saved);
    }

    // 후기 삭제
    @Transactional
    public void deleteReview(Long groupId, Long userId){
        Review review = reviewRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));

        reviewRepository.delete(review);
    }

    //후기 수정

    /*
    // 개별 후기 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByUserId(Long userId){
    }
    */

    // 모임별 후기 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewByGroupId(Long groupId){
        List<Review> reviews = reviewRepository.findByGroupId(groupId);

        return reviews.stream()
                .map(ReviewResponseDto::fromEntity)
                .toList();
    }

    // 특정 유저의 후기 전체 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewByUserId(Long userId){
        List<Review> reviews = reviewRepository.findByUserId(userId);

        return reviews.stream()
                .map(ReviewResponseDto::fromEntity)
                .toList();
    }



}
