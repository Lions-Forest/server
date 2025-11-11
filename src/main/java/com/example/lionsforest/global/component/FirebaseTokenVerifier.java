package com.example.lionsforest.global.component;

import com.example.lionsforest.domain.user.dto.UserInfoDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;

@Component
public class FirebaseTokenVerifier {
    public UserInfoDTO verifyIdToken(String firebaseIdToken) throws AuthenticationException {
        try {
            //1. Firebase Admin SDK로 토큰 검증
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseIdToken);

            //2. Firebase 토큰에서 사용자 정보 추출
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String profilePhoto = decodedToken.getPicture();
            String uid = decodedToken.getUid(); //firebase 고유 uid

            //3. AuthService가 사용하던 UserInfoDTO로 변환
            return UserInfoDTO.builder()
                    .name(name)
                    .email(email)
                    .profile_photo(profilePhoto)
                    .build();

        }catch(FirebaseAuthException e){
            throw new AuthenticationException("Invalid Firebase token: " + e.getMessage());
        }
    }
}
