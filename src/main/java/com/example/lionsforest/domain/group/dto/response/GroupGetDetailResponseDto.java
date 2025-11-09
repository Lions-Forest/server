package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupGetDetailResponseDto {
    private Long id;
    private String title;
    private GroupCategory category;
    private Integer capacity;
    private LocalDateTime meetingAt;
    private String location;
    private GroupState state;

    private List<PhotoDto> photos; // [추가] 전체 사진 목록

    // Group 엔티티를 상세 DTO로 변환
    public static GroupGetDetailResponseDto fromEntity(Group group) {

        List<PhotoDto> photoDtos = group.getPhotos().stream()
                .map(PhotoDto::new) // photo -> new PhotoDto(photo)
                .collect(Collectors.toList());

        return new GroupGetDetailResponseDto(group, photoDtos);
    }

    // private 생성자
    private GroupGetDetailResponseDto(Group group, List<PhotoDto> photos) {
        this.id = group.getId();
        this.title = group.getTitle();
        this.category = group.getCategory();
        this.capacity = group.getCapacity();
        this.meetingAt = group.getMeetingAt();
        this.location = group.getLocation();
        this.state = group.getState();
        this.photos = photos; // [추가]
    }
}
