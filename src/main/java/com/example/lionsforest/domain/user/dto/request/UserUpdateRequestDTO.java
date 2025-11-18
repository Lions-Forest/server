package com.example.lionsforest.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// 유저 정보 수정에 사용
@Getter
@Setter
public class UserUpdateRequestDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "새로운_닉네임")
    private String nickname;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "한 줄 소개")
    private String bio;

    private MultipartFile photo;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean removePhoto;
}
