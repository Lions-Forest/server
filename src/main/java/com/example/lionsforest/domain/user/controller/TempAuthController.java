package com.example.lionsforest.domain.user.controller;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.TokenResponseDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "유저", description = "유저 로그인 관련 API")
public class TempAuthController { //프론트 테스트용 임시 컨트롤러
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자 주입
    public TempAuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/auth/test-token") // SecurityConfig에서 /auth/** 는 permitAll 이라 접근 가능
    @Operation(summary = "임시 로그인", description = "임시로 유저 로그인을 처리하고 액세스 토큰을 발급합니다")
    public ResponseEntity<TokenResponseDTO> getTestToken(
            @RequestParam(defaultValue = "test@example.com") String email
    ) {
        // 1. 테스트 코드의 로직과 동일: 유저 조회 또는 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // User 엔티티 빌더에 맞게 수정하세요
                    User newUser = User.builder()
                            .email(email)
                            .name("테스트유저")
                            .bio("")
                            .nickname("")
                            .profile_photo(null)
                            .build();
                    return userRepository.save(newUser);
                });

        // 2. 토큰 생성
        TokenResponseDTO tokens = jwtTokenProvider.createTokens(user.getId(), user.getEmail());

        // 3. 토큰을 JSON 응답으로 반환
        return ResponseEntity.ok(tokens);
    }
}
