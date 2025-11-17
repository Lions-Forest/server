package com.example.lionsforest.global.component;

import com.example.lionsforest.domain.user.dto.request.LoginRequestDTO;
import com.example.lionsforest.domain.user.dto.request.UserInfoRequestDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Component
public class GoogleOAuthService {

    private final String clientId;
    private final String clientSecret;
    //private final String redirectUri;

    public GoogleOAuthService(
            @Value("${google.auth.client-id}") String clientId,
            @Value("${google.auth.client-secret}") String clientSecret
            //@Value("${google.auth.redirect-uri}") String redirectUri
    ){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        //this.redirectUri = redirectUri;
    }

    //code를 google token으로 교환하고 유저 정보 추출
    public UserInfoRequestDTO getUserInfo(LoginRequestDTO request) {
        try{

            //url-encode된 코드를 rqw code로 디코딩
            //String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

            String code = request.getCode();
            String redirectUri = request.getRedirectUri();
            //code를 token으로 교환
            GoogleTokenResponse tokenResponse = exchangeCodeForToken(code, redirectUri);

            //id token 추출 및 파싱
            String idTokenString = tokenResponse.getIdToken();
            GoogleIdToken idToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), idTokenString);

            //토큰 유효성 검증
            if(!idToken.verifyAudience(Collections.singletonList(clientId))){
                throw new SecurityException("Google ID Token의 Client ID가 유효하지 않습니다.");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();

            //페이로드에서 정보 추출
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profilePhoto = (String) payload.get("picture");

            if(email == null || name == null){
                throw new SecurityException("Google 토큰에서 이메일 또는 이름 정보를 가져올 수 없습니다.");
            }

            return UserInfoRequestDTO.builder()
                    .name(name)
                    .email(email)
                    .profile_photo(profilePhoto)
                    .build();
        } catch(IOException e){
            log.error("Google 인증 코드 교환 또는 토큰 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Google 인증 중 오류가 발생했습니다.", e);
        }
    }

    //구글에 code 보내서 token 받아오는 내부 메서드
    private GoogleTokenResponse exchangeCodeForToken(String code, String redirectUri) throws IOException {
        return new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                "https://oauth2.googleapis.com/token",
                clientId,
                clientSecret,
                code,
                redirectUri
        ).execute();
    }
}
