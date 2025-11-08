package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.dto.request.ParticipationRequestDto;
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
    private String groupTitle;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;

    public static ParticipationResponseDto fromEntity(Participation participation){
        return ParticipationResponseDto.builder()
                .id(participation.getId())
                .groupId(participation.getGroup().getId())
                .groupTitle(participation.getGroup().getTitle())
                .userId(participation.getUser().getId())
                .userName(participation.getUser().getName())
                .createdAt(participation.getCreated_at())
                .build();
    }

}
