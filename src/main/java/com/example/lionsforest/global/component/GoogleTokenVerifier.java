package com.example.lionsforest.global.component;

import com.example.lionsforest.domain.user.dto.request.UserInfoRequestDTO;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
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
                throw new BusinessException(ErrorCode.GOOGLE_CLIENT_ID_CONFIG_ERROR);
            }

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new BusinessException(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profile_photo = (String) payload.get("picture");

            if (email == null || name == null) {
                throw new BusinessException(ErrorCode.GOOGLE_USER_INFO_NOT_FOUND);
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