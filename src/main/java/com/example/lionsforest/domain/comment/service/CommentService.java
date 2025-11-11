package com.example.lionsforest.domain.comment.service;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.repository.CommentRepository;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto createComment(Long groupId, CommentRequestDto dto, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Comment comment = Comment.builder()
                .group(group)
                .content(dto.getContent())
                .user(user)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(saved);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }

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

    // 댓글 좋아요 생성/취소
    @Transactional
    public String toggleLike(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // @ManyToMany의 주인(User) 엔티티를 가져와야 함
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // User 엔티티의 liked_comments Set을 가져옴
        Set<Comment> likedComments = user.getLiked_comments();

        if (likedComments.contains(comment)) {
            // 이미 좋아요 누름 -> 좋아요 취소
            likedComments.remove(comment);
            return "좋아요가 취소되었습니다.";
        } else {
            // 좋아요 안 누름 -> 좋아요 추가
            likedComments.add(comment);
            return "좋아요가 추가되었습니다.";
        }
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

}
