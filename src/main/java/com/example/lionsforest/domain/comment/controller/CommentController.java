package com.example.lionsforest.domain.comment.controller;

import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.service.CommentService;
import com.example.lionsforest.domain.group.dto.request.ParticipationRequestDto;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    // 댓글 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long groupId
                                                    ,@RequestBody CommentRequestDto dto){
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return ResponseEntity.ok(commentService.createComment(groupId, dto, user));
    }

    // 댓글 삭제
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                             @RequestBody CommentRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok("댓글이 삭제 되었습니다.");
    }

    // 모임별 댓글 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<CommentResponseDto>> getCommentByGroup(@PathVariable Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }

    // 특정 댓글 좋아요 (Toggle)
    @PostMapping("/{comment_id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long commentId,
            @RequestBody Map<String, Long> requestBody
    ) {
        Long userId = requestBody.get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        String message = commentService.toggleLike(commentId, user);
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
