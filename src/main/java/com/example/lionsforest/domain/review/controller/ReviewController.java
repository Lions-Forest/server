package com.example.lionsforest.domain.review.controller;

import com.example.lionsforest.domain.review.dto.request.ReviewRequestDto;
import com.example.lionsforest.domain.review.dto.response.ReviewResponseDto;
import com.example.lionsforest.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    // 후기 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<ReviewResponseDto> create(@PathVariable Long groupId,
                                                    @RequestBody ReviewRequestDto dto){
        return ResponseEntity.ok(reviewService.createReview(groupId, dto.getUserId()));
    }

    // 후기 삭제
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long groupId,
                                               @RequestBody ReviewRequestDto dto){
        reviewService.deleteReview(groupId, dto.getUserId());
        return ResponseEntity.ok("후기가 삭제 되었습니다.")
    }

    // 모임별 후기 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewByGroupId(@PathVariable Long groupId){
        return ResponseEntity.ok(reviewService.getReviewByGroupId(groupId));
    }

    // 특정 유저의 후기 전체 조회
    @GetMapping("/{user_id}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }
}
