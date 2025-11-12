package com.example.lionsforest.domain.notification.dto.response;

import com.example.lionsforest.domain.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String content;
    private String photo;
    private boolean read;
    private LocalDateTime createdAt;

    public static NotificationResponseDto fromEntity(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .photo(notification.getPhoto())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
