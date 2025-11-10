package com.example.lionsforest.domain.group.dto.request;

import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.user.User;
import lombok.Getter;
import com.example.lionsforest.domain.group.Group;

import java.time.LocalDateTime;

@Getter
public class GroupRequestDto {
    private String title;
    private GroupCategory category;
    private Integer capacity;
    private LocalDateTime meetingAt;
    private String location;

    public Group toEntity(User leader){
        return Group.builder()
                .title(this.title)
                .category(this.category)
                .capacity(this.capacity)
                .meetingAt(this.meetingAt)
                .location(this.location)
                .state(GroupState.OPEN) // 기본 설정 : 모집중
                .leader(leader)
                .build();
    }

}
