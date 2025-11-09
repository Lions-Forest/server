package com.example.lionsforest.domain.group.dto.request;

import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.user.User;
import lombok.Getter;
import com.example.lionsforest.domain.group.Group;

import java.time.LocalDateTime;

@Getter
public class GroupRequestDto {
    private Long userId;
    private String title;
    private GroupCategory category;
    private Integer count;
    private LocalDateTime meeting_at;
    private String location;
    private GroupState state;

    public Group toEntity(User leader){
        return Group.builder()
                .title(this.title)
                .category(this.category)
                .count(this.count)
                .meeting_at(this.meeting_at)
                .location(this.location)
                .state(this.state)
                .leader(leader)
                .build();
    }

}
