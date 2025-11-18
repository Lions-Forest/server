package com.example.lionsforest.domain.notification.repository;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 유저의 모든 알림 최신순 조회
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 안 읽은 알림 개수 조회
    long countByUserIdAndIsReadFalse(Long userId);

    // 기준시간(cutoff) 이전 생성된 모든 알림 삭제
    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoff")
    int deleteAllByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);

    boolean existsByUserAndContent(User user, String content);
}
