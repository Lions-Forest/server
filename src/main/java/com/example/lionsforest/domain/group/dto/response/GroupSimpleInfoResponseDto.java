package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.*;
import com.example.lionsforest.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Builder
@Getter
public class GroupSimpleInfoResponseDto {
    private Long id;
    private String title;
    private GroupState state;
    private LocalDateTime meetingAt;
    private List<String> participants;
    private GroupCategory category;
    private List<GroupPhotoDto> photos;

    public static GroupSimpleInfoResponseDto fromEntity(Group group) {

        //사진 정렬
        List<GroupPhotoDto> photos = group.getPhotos().stream()
                .sorted(Comparator.comparing(GroupPhoto::getPhotoOrder))
                .map(GroupPhotoDto::new)
                .toList();


        //참여자 이름 리스트로 변환
        List<String> participants = group.getParticipations().stream()
                .map(Participation::getUser)
                .map(User::getName)
                .toList();

        return GroupSimpleInfoResponseDto.builder()
                .id(group.getId())
                .title(group.getTitle())
                .state(group.getState())
                .meetingAt(group.getMeetingAt())
                .participants(participants)
                .category(group.getCategory())
                .photos(photos)
                .build();
    }
}
