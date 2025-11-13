package com.example.lionsforest.global.jwt;

import com.example.lionsforest.domain.user.dto.response.TokenResponseDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j //로깅 위한 어노테이션
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;

    // application.yml 에서 값을 주입받음
    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity-in-ms}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity-in-ms}") long refreshTokenValidity) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMs = accessTokenValidity;
        this.refreshTokenValidityInMs = refreshTokenValidity;
    }

    //액세스 토큰 & 리프레시 토큰 생성
    public TokenResponseDTO createTokens(Long userId, String email) {

        String accessToken = generateToken(userId, email, accessTokenValidityInMs, "Access");
        String refreshToken = generateToken(userId, email, refreshTokenValidityInMs, "Refresh"); // Refresh Token에도 기본 정보 포함

        return TokenResponseDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 실제 토큰을 생성하는 메서드
    private String generateToken(Long userId, String email, long validityMs, String tokenType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(userId)) //토큰 주체(유저 아이디)
                .setIssuedAt(now) //발급 시간
                .setExpiration(validity); //만료 시간
        if("Access".equals(tokenType)) { //액세스 토큰에만 email, auth 클레임 추가
            builder.claim("email", email) //커스텀 클레임(이메일)
                    .claim("auth", "ROLE_USER"); //권한 정보 클레임 추가
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    // 토큰에서 유저 아이디 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    //토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    //토큰에서 정보 파싱 - 추출
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 Claims는 정상적으로 파싱될 수 있으므로 반환
            return e.getClaims();
        }
    }

    //토큰을 받아 Authentication 객체 반환
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 반환
        // User(Spring Security) 객체를 생성, sub 클레임(userId)을 principal로 사용
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
