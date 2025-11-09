package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.LoginResponseDTO;
import com.example.lionsforest.domain.user.dto.TokenResponseDTO;
import com.example.lionsforest.domain.user.dto.UserInfoDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.component.GoogleTokenVerifier;
import com.example.lionsforest.global.component.MemberWhitelistValidator;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final MemberWhitelistValidator whitelistValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenVerifier googleTokenVerifier;

    public LoginResponseDTO googleLoginOrRegister(LoginRequestDTO request) {

        UserInfoDTO googleUserInfo = googleTokenVerifier.verify(request.getIdToken());
        String name = googleUserInfo.getName();
        String email = googleUserInfo.getEmail();

        if (!whitelistValidator.isMember(name, email)) {
            throw new SecurityException("동아리 부원 명단에 존재하지 않거나 정보가 일치하지 않습니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        boolean isNewUser = false;
        User user;

        if (optionalUser.isEmpty()) {
            user = googleUserInfo.toEntity();
            userRepository.save(user);
            isNewUser = true;
            System.out.println("새 유저 생성!");
        } else {
            user = optionalUser.get();
            System.out.println("이미 존재하는 유저");
        }

        // 6. JWT 토큰 생성 (반환 타입이 TokenResponse DTO임)
        TokenResponseDTO tokens = jwtTokenProvider.createTokens(user.getId(), user.getEmail());

        // 7. 응답 DTO 생성
        return LoginResponseDTO.builder()
                .id(user.getId())
                .accessToken(tokens.getAccessToken()) // TokenResponse DTO의 getter
                .refreshToken(tokens.getRefreshToken()) // TokenResponse DTO의 getter
                .isNewUser(isNewUser)
                .nickname(user.getNickname())
                .build();
    }
}