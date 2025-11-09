package com.example.lionsforest.domain.user.controller;

import com.example.lionsforest.domain.user.dto.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.LoginResponseDTO;
import com.example.lionsforest.domain.user.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    //구글 로그인(회원가입)
    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(
            @Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.googleLoginOrRegister(request);
        return ResponseEntity.ok(response);
    }
}