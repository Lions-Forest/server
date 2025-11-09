package com.example.lionsforest.domain.comment.repository;

import com.example.lionsforest.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 모임별 댓글 조회
    List<Comment> findByGroupId(Long groupId);
    // 유저별 댓글 조회
    List<Comment> findByUserId(Long userId);
}
