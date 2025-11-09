package com.example.lionsforest.domain.review.dto.response;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long groupId;
    private String groupTitle;
    private Long userId;
    private String userName;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;

    public static ReviewResponseDto fromEntity(Review review){
        return ReviewResponseDto.builder()
                .id(review.getReview_id())
                .groupId(review.getGroup().getId())
                .groupTitle(review.getGroup().getTitle())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .content(review.getContent())
                .score(review.getScore())
                .createdAt(review.getCreated_at())
                .build();
    }
}
