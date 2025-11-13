package com.example.lionsforest.domain.user.controller;

import com.example.lionsforest.domain.user.dto.request.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.response.LoginResponseDTO;
import com.example.lionsforest.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    //구글 로그인(회원가입)
    @PostMapping("/google")
    @Operation(summary = "유저 회원가입 및 로그인", description = "firebase token으로 유저 로그인을 처리합니다")
    public ResponseEntity<LoginResponseDTO> googleLogin(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.googleLoginOrRegister(request);
        return ResponseEntity.ok(response);
    }
}