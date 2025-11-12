package com.example.lionsforest.domain.notification.service;

import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCleanupService {

    private NotificationRepository notificationRepository;

    // 매일 새벽 2시에 실행
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int deletedCount = notificationRepository.deleteAllByCreatedAtBefore(cutoff);
        log.info("Deleted {} notifications older than 30 days", deletedCount);
    }
}

