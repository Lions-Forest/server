package com.example.lionsforest.global.config;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalHandler {

    // SecurityContext에서 현재 로그인한 사용자의 ID (Long)를 가져옴
    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다. (SecurityContext is empty)");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // JwtTokenProvider에서 UserDetails의 username 필드에 userId를 문자열로 저장했음
            String userIdString = ((UserDetails) principal).getUsername();
            try {
                return Long.parseLong(userIdString);
            } catch (NumberFormatException e) {
                throw new RuntimeException("유효하지 않은 사용자 ID 형식입니다.", e);
            }
        } else if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new RuntimeException("인증되지 않은 사용자입니다. (anonymousUser)");
        }

        throw new RuntimeException("유효한 인증 주체(Principal)를 찾을 수 없습니다.");
    }
}