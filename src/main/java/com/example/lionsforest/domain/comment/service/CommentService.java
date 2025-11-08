package com.example.lionsforest.domain.comment.service;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.repository.CommentRepository;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto createComment(Long groupId, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Comment comment = Comment.builder()
                .group(group)
                .user(user)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(saved);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long groupId, Long userId){
        Comment comment = commentRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        commentRepository.delete(comment);
    }

    // 모임별 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByGroupId(Long groupId){
        List<Comment> comments = commentRepository.findByGroupId(groupId);

        return comments.stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
    }
/*
    // 유저별 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByUserId(Long userId){
        List<Comment> comments = commentRepository.findByUserId(userId);

        return comments.stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
    }
 */

    // 댓글 좋아요 생성/취소

}
