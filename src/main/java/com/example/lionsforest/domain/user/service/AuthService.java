package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.request.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.response.LoginResponseDTO;
import com.example.lionsforest.domain.user.dto.response.TokenResponseDTO;
import com.example.lionsforest.domain.user.dto.request.UserInfoRequestDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.component.FirebaseTokenVerifier;
import com.example.lionsforest.global.component.GoogleOAuthService;
import com.example.lionsforest.global.component.GoogleTokenVerifier;
import com.example.lionsforest.global.component.MemberWhitelistValidator;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
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
    private final NicknameService nicknameService;
    private final GoogleOAuthService googleOAuthService;

    /*@Value("${google.auth.client-id}")
    private String clientId;

    @Value("${google.auth.client-secret}")
    private String clientSecret;

    @Value("${google.auth.redirect-uri}")
    private String redirectUri;*/

    //code를 받아 로그인 처리하는 메소드(메인)
    public LoginResponseDTO loginWithGoogleCode(LoginRequestDTO loginRequestDTO) {
        UserInfoRequestDTO userInfo = googleOAuthService.getUserInfo(loginRequestDTO);

        return processUserLogin(userInfo);
    }

    public LoginResponseDTO processUserLogin(UserInfoRequestDTO userInfo) {

        String name = userInfo.getName();
        String email = userInfo.getEmail();

        //동아리 부원인지 조회
        if (!whitelistValidator.isMember(name, email)) {
            throw new BusinessException(ErrorCode.USER_NOT_IN_WHITELIST);
        }

        //첫 가입인지 확인한 후 로그인 시킴
        Optional<User> optionalUser = userRepository.findByEmail(email);
        boolean isNewUser = false;
        User user;

        if (optionalUser.isEmpty()) {
            user = userInfo.toEntity();
            String userNickname = nicknameService.generateRandomNickname("");
            user.setNickname(userNickname);
            userRepository.save(user);
            isNewUser = true;
            log.info("새 유저 생성!");
        } else {
            user = optionalUser.get();
            log.info("이미 존재하는 유저");
        }

        // Firebase 커스텀 토큰 생성
        String firebaseToken;
        try{
            //우리 DB의 userid를 firebase의 uid로 사용
            String uid = String.valueOf(user.getId());
            firebaseToken = FirebaseAuth.getInstance().createCustomToken(uid);
        }catch(FirebaseAuthException e){
            log.error("Firebase 커스텀 토큰 생성 실패(User ID: {}): {}", user.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.FIREBASE_TOKEN_CREATION_FAILED);
        }

        // JWT 토큰 생성 (반환 타입이 TokenResponse DTO임)
        TokenResponseDTO tokens = jwtTokenProvider.createTokens(user.getId(), user.getEmail());

        // 응답 DTO 생성
        return LoginResponseDTO.builder()
                .id(user.getId())
                .accessToken(tokens.getAccessToken()) // TokenResponse DTO의 getter
                .refreshToken(tokens.getRefreshToken()) // TokenResponse DTO의 getter
                .isNewUser(isNewUser)
                .nickname(user.getNickname())
                .firebaseToken(firebaseToken)
                .build();
    }
}