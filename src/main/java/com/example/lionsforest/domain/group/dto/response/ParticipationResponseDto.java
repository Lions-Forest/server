package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.Participation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ParticipationResponseDto {
    private Long id;
    private Long groupId;
    private Long userId;
    private String userName;
    private String userNickname;
    private LocalDateTime createdAt;

    public static ParticipationResponseDto fromEntity(Participation participation){
        return ParticipationResponseDto.builder()
                .id(participation.getId())
                .groupId(participation.getGroup().getId())
                .userId(participation.getUser().getId())
                .userName(participation.getUser().getName())
                .userNickname(participation.getUser().getNickname())
                .createdAt(participation.getCreatedAt())
                .build();
    }

}
