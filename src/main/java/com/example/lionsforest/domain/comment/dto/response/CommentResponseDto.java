package com.example.lionsforest.domain.comment.dto.response;

import com.example.lionsforest.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long groupId;
    private Long userId;
    private String profilePhotoUrl;
    private String userName;
    private String userNickName;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;

    public static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getCommentId())
                .groupId(comment.getGroup().getId())
                .userId(comment.getUser().getId())
                .profilePhotoUrl(comment.getUser().getProfile_photo())
                .userName(comment.getUser().getName())
                .userNickName(comment.getUser().getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikedByUsers().size()) // 좋아요 수
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
