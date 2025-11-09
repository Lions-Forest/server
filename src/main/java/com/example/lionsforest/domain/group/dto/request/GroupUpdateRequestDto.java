package com.example.lionsforest.domain.group.dto.request;

import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupUpdateRequestDto {
    private Long userId;
    private String title;
    private GroupCategory category;
    private Integer capacity;
    private LocalDateTime meetingAt;
    private String location;
    private GroupState state;
}
