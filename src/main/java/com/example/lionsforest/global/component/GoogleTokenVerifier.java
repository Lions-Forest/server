package com.example.lionsforest.global.component;

import com.example.lionsforest.domain.user.dto.request.UserInfoRequestDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;
    private final String clientId;

    public GoogleTokenVerifier(@Value("${google.auth.client-id}") String clientId) {
        this.clientId = clientId;
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public UserInfoRequestDTO verify(String idToken) {
        try {
            if (clientId == null || clientId.isBlank() || clientId.contains("YOUR_GOOGLE_CLIENT_ID")) {
                throw new IllegalArgumentException("Google Client ID가 application.yml에 설정되지 않았습니다.");
            }

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new SecurityException("유효하지 않은 구글 ID 토큰입니다.");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profile_photo = (String) payload.get("picture");

            if (email == null || name == null) {
                throw new SecurityException("구글 토큰에서 이메일 또는 이름 정보를 가져올 수 없습니다.");
            }

            return UserInfoRequestDTO.builder()
                    .name(name)
                    .email(email)
                    .profile_photo(profile_photo)
                    .build();

        } catch (Exception e) {
            throw new SecurityException("구글 ID 토큰 검증에 실패했습니다. " + e.getMessage(), e);
        }
    }
}