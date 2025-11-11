package com.example.lionsforest;

import com.example.lionsforest.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenGenerationTest { // 클래스 이름은 아무거나 상관없습니다.

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void generateTestToken() {
        // --- 여기만 수정 ---
        Long testUserId = 1L; // DB에 있는 테스트용 유저 ID
        String testUserEmail = "test@example.com"; // 해당 유저 이메일
        // -----------------

        // 토큰 생성
        var tokenResponseDTO = jwtTokenProvider.createTokens(testUserId, testUserEmail);
        String accessToken = tokenResponseDTO.getAccessToken();

        // 토큰을 콘솔에 출력
        System.out.println("--- Generated Access Token ---");
        System.out.println(accessToken);
        System.out.println("---------------------------------");
    }
}