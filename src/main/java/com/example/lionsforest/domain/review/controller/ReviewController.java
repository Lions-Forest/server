package com.example.lionsforest.domain.review.controller;

import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.review.dto.request.ReviewRequestDto;
import com.example.lionsforest.domain.review.dto.request.ReviewUpdateRequestDto;
import com.example.lionsforest.domain.review.dto.response.ReviewGetResponseDto;
import com.example.lionsforest.domain.review.dto.response.ReviewResponseDto;
import com.example.lionsforest.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    // 후기 생성
    @PostMapping(value = "/{group_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable("group_id") Long groupId,
                                                    @RequestPart("dto") ReviewRequestDto dto,
                                                    @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(reviewService.createReview(groupId, dto, photos, loginUserId));
    }

    // 후기 수정
    @PatchMapping(value = "/{review_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("review_id") Long reviewId,
                                                          @RequestPart("dto") ReviewUpdateRequestDto dto,
                                                          @RequestPart(value = "addPhotos", required = false) List<MultipartFile> addPhotos,
                                                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(reviewService.updateReview(reviewId, dto, addPhotos, loginUserId));
    }

    // 개별 후기 조회
    @GetMapping("/{review_id}")
    public ResponseEntity<ReviewGetResponseDto> getReviewById(@PathVariable("review_id") Long reviewId){
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    // 모임별 후기 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<ReviewGetResponseDto>> getReviewByGroupId(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(reviewService.getReviewByGroupId(groupId));
    }

    // 특정 유저의 후기 전체 조회
    @GetMapping("/{user_id}")
    public ResponseEntity<List<ReviewGetResponseDto>> getReviewByUserId(@PathVariable("group_id") Long userId){
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }

    // 후기 삭제
    @DeleteMapping("/{review_id}")
    public ResponseEntity<String> deleteReview(@PathVariable("review_id") Long reviewId,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){

        Long loginUserId = Long.valueOf(principal.getUsername());

        reviewService.deleteReview(reviewId, loginUserId);
        return ResponseEntity.ok("후기가 삭제 되었습니다.");
    }
}
