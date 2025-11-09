package com.example.lionsforest.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 세션 관리 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 폼 로그인/HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. OAuth2 로그인 페이지 비활성화
                //    이걸 꺼야 /auth/google 요청이 컨트롤러로 감
                .oauth2Login(AbstractHttpConfigurer::disable)

                // 5. API 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // "/auth/**" 경로는 인증 없이 무조건 통과(permitAll)
                        .requestMatchers("/auth/**").permitAll()

                        // 추후 "/api/**" 등 다른 엔드포인트는 인증(JWT)이 필요하도록 설정
                        .anyRequest().authenticated()
                );

        // 추후 JWT 필터 추가 필요

        return http.build();
    }
}