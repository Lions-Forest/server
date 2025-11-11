package com.example.lionsforest.domain.comment.controller;

import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/{group_id}")
    @Operation(summary = "댓글 생성", description = "특정 모임(By group_id)에 대한 댓글을 작성합니다")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable("group_id") Long groupId,
                                                            @RequestBody CommentRequestDto dto,
                                                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(commentService.createComment(groupId, dto, loginUserId));
    }

    // 모임별 댓글 조회
    @GetMapping("/{group_id}")
    @Operation(summary = "모임별 댓글 조회", description = "특정 모임(By group_id)에 대한 댓글을 조회합니다")
    public ResponseEntity<List<CommentResponseDto>> getCommentByGroup(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }

    // 댓글 삭제
    @DeleteMapping("/{comment_id}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글(By comment_id)을 삭제합니다")
    public ResponseEntity<String> deleteComment(@PathVariable("comment_id") Long commentId,
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Long loginUserId = Long.valueOf(principal.getUsername());

        commentService.deleteComment(commentId, loginUserId);
        return ResponseEntity.ok("댓글이 삭제 되었습니다.");
    }

    // 특정 댓글 좋아요 (Toggle)
    @PostMapping("/{comment_id}/like")
    @Operation(summary = "특정 댓글 좋아요 생성/삭제(Toggle)", description = "특정 댓글(By comment_id)에 대한 좋아요를 생성/삭제")
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
