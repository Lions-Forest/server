package com.example.lionsforest;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class TokenGenerationTest { // 클래스 이름은 아무거나 상관없습니다.

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Commit
    void generateTestToken() {
        //DB에서 이메일로 유저 찾거나 생성
        String testUserEmail = "test@example.com";
        User user = userRepository.findByEmail(testUserEmail)
                .orElseGet(() -> {
                    User testUser = User.builder()
                            .email(testUserEmail)
                            .name("테스트유저")
                            .build();
                    return userRepository.save(testUser);
                });
        Long testUserId = user.getId();

        // 토큰 생성
        var tokenResponseDTO = jwtTokenProvider.createTokens(testUserId, testUserEmail);
        String accessToken = tokenResponseDTO.getAccessToken();

        // 토큰을 콘솔에 출력
        System.out.println("--- Generated Access Token ---");
        System.out.println(accessToken);
        System.out.println("---------------------------------");
    }
}