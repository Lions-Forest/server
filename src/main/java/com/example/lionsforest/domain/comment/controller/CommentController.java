package com.example.lionsforest.domain.comment.controller;

import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.service.CommentService;
import com.example.lionsforest.domain.group.dto.request.ParticipationRequestDto;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/{group_id}")
    public ResponseEntity<CommentResponseDto> create(@PathVariable Long groupId
                                                    ,@RequestBody CommentRequestDto dto){
        return ResponseEntity.ok(commentService.createComment(groupId, dto.getUserId()));
    }

    // 댓글 삭제
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> leaveGroup(@PathVariable Long groupId,
                                             @RequestBody CommentRequestDto dto) {
        commentService.deleteComment(groupId, dto.getUserId());
        return ResponseEntity.ok("댓글이 삭제 되었습니다.");
    }

    // 모임별 댓글 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<CommentResponseDto>> getUser(@PathVariable Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }
/*
    // 유저별 댓글 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<CommentResponseDto>> getUser(@PathVariable Long groupId){
        return ResponseEntity.ok(commentService.getCommentsByGroupId(groupId));
    }
*/
}
