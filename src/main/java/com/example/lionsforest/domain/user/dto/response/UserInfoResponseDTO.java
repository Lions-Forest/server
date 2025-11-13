package com.example.lionsforest.domain.user.dto.response;

import com.example.lionsforest.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String bio;
    private String profile_photo;

    // User 엔티티를 InfoResponse DTO로 변환
    public static UserInfoResponseDTO from(User user) {
        return UserInfoResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bio(user.getBio())
                .profile_photo(user.getProfile_photo())
                .build();
    }
}
