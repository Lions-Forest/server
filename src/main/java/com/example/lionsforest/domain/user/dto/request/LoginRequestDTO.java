package com.example.lionsforest.domain.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDTO {
    private String code;
    private String redirectUri;
}