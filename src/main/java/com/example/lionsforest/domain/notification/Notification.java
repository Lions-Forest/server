package com.example.lionsforest.domain.notification;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    private String photo;

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    private Long targetId;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    public void markRead() { this.isRead = true; }
}
