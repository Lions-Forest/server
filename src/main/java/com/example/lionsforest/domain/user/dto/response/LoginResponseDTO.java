package com.example.lionsforest.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

//유저 로그인 응답에 사용
@Builder
@Getter
public class LoginResponseDTO {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private boolean isNewUser; //최초 가입 여부
    private String nickname;
    private String firebaseToken;
}
