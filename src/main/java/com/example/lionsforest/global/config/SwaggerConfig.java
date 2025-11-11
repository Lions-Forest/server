package com.example.lionsforest.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.awt.SystemColor.info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("API 제목")
                .description("API 설명")
                .version("v1.0.0");


        // 2. [추가] SecurityScheme 이름 정의
        String securitySchemeName = "bearerAuth";

        // 3. [추가] SecurityRequirement 생성 (전역 자물쇠 설정)
        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(securitySchemeName);

        // 4. [추가] SecurityScheme 정의 (JWT Bearer 방식)
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")               // bearer 토큰 사용
                        .bearerFormat("JWT"));          // JWT 포맷

        // 5. OpenAPI 객체 생성 및 설정 적용
        return new OpenAPI()
                .info(info) // 1번 Info 적용
                .addSecurityItem(securityRequirement) // 3번 SecurityRequirement 적용
                .components(components);              // 4번 Components 적용
    }
}
