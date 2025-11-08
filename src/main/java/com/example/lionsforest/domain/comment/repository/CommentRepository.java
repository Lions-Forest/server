package com.example.lionsforest.domain.comment.repository;

import com.example.lionsforest.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByGroupIdAndUserId(Long groupId, Long UserId);

    List<Comment> findByGroupId(Long groupId);
    List<Comment> findByUserId(Long userId);
}
