package com.example.lionsforest.domain.notification.controller;

import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.dto.response.NotificationResponseDto;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/")
@Tag(name = "알림", description = "알림 관련 API")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    // 알림 목록 조회
    @GetMapping("{user_id}/")
    @Operation(summary = "알림 목록 조회", description = "알림을 모두 조회합니다")
    public List<NotificationResponseDto> getNotifications(@PathVariable(value = "user_id") Long userId) {
        // 특정 사용자에 대한 모든 알림을 최신순으로 가져오기
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        // DTO로 변환하여 반환
        return notifications.stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 안읽은 알림 개수 조회
    @GetMapping("{user_id}/unread/count/")
    @Operation(summary = "안읽은 알림 개수 조회", description = "안읽은 알림 개수를 조회합니다")
    public long getNotificationsUnreadCount(@PathVariable(value = "user_id") Long userId) {

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        return unreadCount;
    }

    // 알림 읽음 처리
    @PostMapping("{notification_id}/read/")
    @Operation(summary = "알림 읽음 처리", description = "알림을 '읽음' 상태로 바꿉니다")
    public void markAsRead(@PathVariable(value = "notification_id") Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 알림 ID입니다."));
        notification.markRead();  // read 필드를 true로
        notificationRepository.save(notification);
    }
}

