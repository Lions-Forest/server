package com.example.lionsforest.domain.radar;

import com.example.lionsforest.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Radar {
    @Id //1:1 관계이므로 user의 pk를 그대로 pk로 사용
    @Column(name = "user_id")
    private Long user_id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId //'userId' 필드가 이 관계에 매핑됨
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean enable;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private RadarState state;

    private String message;

    private LocalDateTime updated_at;

    private Integer likes;

}
