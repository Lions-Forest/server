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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/")
@Tag(name = "후기", description = "후기 관련 API")
public class ReviewController {
    private final ReviewService reviewService;

    // 후기 생성
    @PostMapping(value = "{group_id}/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "후기 생성", description = """
        요청 형식: multipart/form-data
        - score : Integer
        - content : string
        - title: string
        - photos: 이미지 파일 여러 개 (동일 키 'photos'로 append)
        
             ### 💻 프론트 전송 예시 (Axios)
                     ```javascript
                     const form = new FormData();
                     form.append("score", "3");
                     form.append("content", "후기 내용");
                     files.forEach(f => form.append("photos", f)); // 동일 키로 여러 번 append
            
                     await axios.post("/api/reviews/", form, {
                       headers: { "Content-Type": "multipart/form-data" }
                     });
            
        """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(   // Swagger 문서화용
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ReviewRequestDto.class)
            ))
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable("group_id") Long groupId,
                                                          @ModelAttribute ReviewRequestDto req,
                                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(reviewService.createReview(groupId, req, loginUserId));
    }

    // 후기 수정
    @PatchMapping(value = "{review_id}/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "후기 수정", description = """
        요청 형식: multipart/form-data
        - score : Integer
        - content : string
        - title: string
        - deletePhotoIds : List<Long> 삭제할 사진의 아이디 리스트
        - photos: 이미지 파일 여러 개 (동일 키 'photos'로 append)
        
             ### 💻 프론트 전송 예시 (Axios)
                     ```javascript
                     const form = new FormData();
                     form.append("score", "3");
                     form.append("content", "후기 내용");
                     form.append("deletePhotoIds", JSON.stringify([2, 5]));
                     files.forEach(f => form.append("addPhotos", f)); // 동일 키로 여러 번 append
            
                     await axios.patch("/api/reviews/${review_id}/", form, {
                       headers: { "Content-Type": "multipart/form-data" }
                     });
            
        """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(   // Swagger 문서화용
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ReviewUpdateRequestDto.class)
            ))
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("review_id") Long reviewId,
                                                          @ModelAttribute ReviewUpdateRequestDto req,
                                                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(reviewService.updateReview(reviewId, req, loginUserId));
    }

    // 개별 후기 조회
    @GetMapping("{review_id}/")
    @Operation(summary = "개별 후기 조회", description = "특정 후기(By review_id)를 조회합니다")
    public ResponseEntity<ReviewGetResponseDto> getReviewById(@PathVariable("review_id") Long reviewId){
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));

    }

    // 모임별 후기 조회
    @GetMapping("by-group/{group_id}/")
    @Operation(summary = "모임별 후기 조회", description = "특정 모임(By group_id)에 대한 후기를 조회합니다")
    public ResponseEntity<List<ReviewGetResponseDto>> getReviewByGroupId(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(reviewService.getReviewByGroupId(groupId));
    }

    // 특정 유저의 후기 전체 조회
    @GetMapping("by-user/{user_id}/")
    @Operation(summary = "유저별 후기 조회", description = "특정 유저(By user_id)에 대한 후기를 조회합니다")
    public ResponseEntity<List<ReviewGetResponseDto>> getReviewByUserId(@PathVariable("user_id") Long userId){
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }

    // 후기 삭제
    @DeleteMapping("{review_id}/")
    @Operation(summary = "후기 삭제", description = "특정 후기(review_id)를 삭제합니다")
    public ResponseEntity<String> deleteReview(@PathVariable("review_id") Long reviewId,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){

        Long loginUserId = Long.valueOf(principal.getUsername());

        reviewService.deleteReview(reviewId, loginUserId);
        return ResponseEntity.ok("후기가 삭제 되었습니다.");
    }
}
