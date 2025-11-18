package com.example.lionsforest.domain.notification.service;

import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.TargetType;
import com.example.lionsforest.domain.notification.dto.response.NotificationResponseDto;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 레이더 좋아요 알림 생성
    public NotificationResponseDto createRadarLikeNotification(Long senderId, Long receiverId) {
        User sender = findUserById(senderId);
        User receiver = findUserById(receiverId);

        String senderNickname = sender.getNickname();
        System.out.println("senderNickname: " + senderNickname);
        String content = String.format("♥\uFE0F 나의 상태메시지에 '%s'님이 하트를 달았어요", senderNickname);

        Notification notification = Notification.builder()
                .user(receiver)
                .content(content)
                .photo(receiver.getProfile_photo())
                .isRead(false)
                .targetId(receiverId)
                .targetType(TargetType.GROUP)
                .build();
        notificationRepository.save(notification);

        return NotificationResponseDto.fromEntity(notification);

    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
