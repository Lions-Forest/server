package com.example.lionsforest.domain.comment.controller;

import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.service.CommentService;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable("group_id") Long groupId,
                                                            @RequestBody CommentRequestDto dto,
                                                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(commentService.createComment(groupId, dto, loginUserId));
    }

    // 댓글 삭제
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable("comment_id") Long commentId,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Long loginUserId = Long.valueOf(principal.getUsername());

        commentService.deleteComment(commentId, loginUserId);
        return ResponseEntity.ok("댓글이 삭제 되었습니다.");
    }

    // 모임별 댓글 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<CommentResponseDto>> getCommentByGroup(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }

    // 특정 댓글 좋아요 (Toggle)
    @PostMapping("/{comment_id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable("comment_id") Long commentId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        Long loginUserId = Long.valueOf(principal.getUsername());

        String message = commentService.toggleLike(commentId, loginUserId);
        return ResponseEntity.ok(message);
    }
/*
    // 유저별 댓글 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<CommentResponseDto>> getUser(@PathVariable Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }
*/
}
