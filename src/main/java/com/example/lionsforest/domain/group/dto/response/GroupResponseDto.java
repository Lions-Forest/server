package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GroupResponseDto {
    private Long id;
    private Long leaderId;
    private String leaderNickname;
    private String leaderName;
    private String title;
    private GroupCategory category;
    private Integer capacity;
    private LocalDateTime meetingAt;
    private String location;
    private GroupState state;
    private int participantCount;

    public static GroupResponseDto fromEntity(Group group){
        return GroupResponseDto.builder()
                .id(group.getId())
                .leaderId(group.getLeader().getId())
                .leaderNickname(group.getLeader().getNickname())
                .leaderName(group.getLeader().getName())
                .title(group.getTitle())
                .category(group.getCategory())
                .capacity(group.getCapacity())
                .meetingAt(group.getMeetingAt())
                .location(group.getLocation())
                .state(group.getState())
                .participantCount(group.getParticipations().size()) // 현재 참여자 수
                .build();
    }
}
