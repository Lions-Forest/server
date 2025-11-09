package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.GroupState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@Builder
@AllArgsConstructor
public class GroupGetResponseDto {
    private Long id;
    private String title;
    private GroupCategory category;
    private Integer capacity;
    private LocalDateTime meetingAt;
    private String location;
    private GroupState state;

    private String thumbnailUrl;

    public static GroupGetResponseDto fromEntity(Group group){

        String thumbnailUrl = group.getPhotos().stream()
                .min(Comparator.comparing(GroupPhoto::getPhoto_order)) // photo_order가 가장 낮은 (첫 번째) 사진
                .map(GroupPhoto::getPhoto) // 사진의 URL을 가져옴
                .orElse(null); // 사진이 없으면 null

        return GroupGetResponseDto.builder()
                .id(group.getId())
                .title(group.getTitle())
                .category(group.getCategory())
                .capacity(group.getCapacity())
                .meetingAt(group.getMeetingAt())
                .location(group.getLocation())
                .state(group.getState())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
