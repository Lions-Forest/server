package com.example.lionsforest.global.config;


import com.example.lionsforest.global.jwt.JwtAuthenticationFilter;
import com.example.lionsforest.global.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
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
                        .requestMatchers("/auth/**","/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        //api 경로: 인증 되면 통과
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        // 추후 JWT 필터 추가 필요

        return http.build();
    }
}