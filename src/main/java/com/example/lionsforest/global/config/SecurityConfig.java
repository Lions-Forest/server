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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 관리 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 폼 로그인/HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // OAuth2 로그인 페이지 비활성화
                //    이걸 꺼야 /auth/google 요청이 컨트롤러로 감
                .oauth2Login(AbstractHttpConfigurer::disable)

                // API 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 무조건 통과(permitAll)하는 경로
                        .requestMatchers("/auth/**","/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        //api 및 다른 경로: 인증 되면 통과
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                //jwt 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    //CORS 설정 위한 Bean
    //프론트엔드 도메인에서의 요청을 허용함
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        //허용할 origin: 프론트엔드 도메인(https://lions-forest.p-e.kr) & 로컬 개발 환경
        config.setAllowedOrigins(Arrays.asList(
                //프론트엔드 주소
                "https://lions-forest.p-e.kr",
                "http://lions-forest.p-e.kr",
                "http://localhost:3000",
                "http://localhost:5173",
                "https://lionforest-dev.netlify.app",
                //백엔드 주소
                "https://api.lions-forest.p-e.kr",
                "http://localhost:8080"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); //허용할 http 메서드
        config.setAllowedHeaders(Arrays.asList("*")); //모든 http 헤더 허용
        config.setAllowCredentials(true); // 자격 증명(쿠키, authorization 헤더) 허용
        config.setMaxAge(3600L); //요청 캐시 시간: 1시간

        //위 설정을 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}