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
    private String groupTitle;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;

    public static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getComment_id())
                .groupId(comment.getGroup().getId())
                .groupTitle(comment.getGroup().getTitle())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
