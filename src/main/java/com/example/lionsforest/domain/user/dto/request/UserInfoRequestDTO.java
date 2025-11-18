package com.example.lionsforest.domain.user.dto.request;

import com.example.lionsforest.domain.user.User;
import lombok.Builder;
import lombok.Getter;

// 유저 로그인에 사용
// GoogleTokenVerifier가 반환하는 내부 전용 DTO
@Getter
@Builder
public class UserInfoRequestDTO {
    private String name;
    private String email;
    private String profile_photo;


    // 최초 로그인(회원가입) 시 사용
    public User toEntity(){
        return User.builder()
                .name(this.name)
                .email(this.email)
                .profile_photo(this.profile_photo)
                .build();
    }
}
