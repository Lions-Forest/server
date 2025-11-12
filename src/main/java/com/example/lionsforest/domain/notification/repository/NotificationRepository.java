package com.example.lionsforest.domain.notification.repository;

import com.example.lionsforest.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 유저의 모든 알림 최신순 조회
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 안 읽은 알림 개수 조회
    long countByUserIdAndIsReadFalse(Long userId);
}
