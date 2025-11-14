package com.example.lionsforest.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// 유저 정보 수정에 사용
@Getter
@Setter
public class UserUpdateRequestDTO {
    private String nickname;
    private String bio;
    private MultipartFile photo;
    private Boolean removePhoto;
}
