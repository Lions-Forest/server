package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.request.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.response.LoginResponseDTO;
import com.example.lionsforest.domain.user.dto.response.TokenResponseDTO;
import com.example.lionsforest.domain.user.dto.request.UserInfoRequestDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.component.FirebaseTokenVerifier;
import com.example.lionsforest.global.component.GoogleTokenVerifier;
import com.example.lionsforest.global.component.MemberWhitelistValidator;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final NicknameService nicknameService;

    public LoginResponseDTO googleLoginOrRegister(LoginRequestDTO request) {

        //request DTO에서 idToken 꺼내기
        String idToken = request.getIdToken();
        //firebasetokenverifier가 토큰 검증 -> 사용자 정보 추출
        UserInfoRequestDTO userInfo = googleTokenVerifier.verify(idToken);
        String name = userInfo.getName();
        String email = userInfo.getEmail();

        //동아리 부원인지 조회
        if (!whitelistValidator.isMember(name, email)) {
            throw new SecurityException("동아리 부원 명단에 존재하지 않거나 정보가 일치하지 않습니다.");
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
            System.out.println("새 유저 생성!");
        } else {
            user = optionalUser.get();
            System.out.println("이미 존재하는 유저");
        }

        // Firebase 커스텀 토큰 생성
        String firebaseToken;
        try{
            //우리 DB의 userid를 firebase의 uid로 사용
            String uid = String.valueOf(user.getId());
            firebaseToken = FirebaseAuth.getInstance().createCustomToken(uid);
        }catch(FirebaseAuthException e){
            log.error("Firebase 커스텀 토큰 생성 실패(User ID: {}): {}", user.getId(), e.getMessage());
            throw new RuntimeException("Firebase 토큰 생성 중 오류가 발생했습니다.", e);
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