package com.example.lionsforest.domain.user.dto;

import lombok.Getter;

// 유저 정보 수정에 사용
@Getter
public class UserUpdateRequestDTO {
    private String nickname;
    private String bio;
    private String profile_photo;
}
