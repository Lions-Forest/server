package com.example.lionsforest.global.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        // 2. validateToken으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가져와 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // (로그) 유저 ID가 SecurityContext에 저장됨
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
        }

        // 4. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보를 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값 반환
        }
        return null;
    }
}