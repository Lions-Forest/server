package com.example.lionsforest.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponseDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
