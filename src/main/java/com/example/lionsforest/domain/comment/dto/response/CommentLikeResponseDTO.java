package com.example.lionsforest.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentLikeResponseDTO {
    private final boolean isLiked;

    // 정적 팩토리 메서드
    public static CommentLikeResponseDTO of(boolean isLiked) {
        return CommentLikeResponseDTO.builder()
                .isLiked(isLiked)
                .build();
    }
}
