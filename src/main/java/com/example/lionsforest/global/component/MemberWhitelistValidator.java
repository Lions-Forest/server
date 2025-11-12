package com.example.lionsforest.global.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j //로깅 위한 어노테이션
@Component
public class MemberWhitelistValidator {
    // (ㅇㅣ메일, 이름) 저장 맵
    private final Map<String, String> whitelist = new HashMap<>();

    @PostConstruct
    public void loadWhitelist() {
        //members.txt 로드
        ClassPathResource resource = new ClassPathResource("members.txt");

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            // txt 파일 끝까지 파싱 -> (이메일, 이름) 저장
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String email = parts[1].trim();
                    whitelist.put(email, name); // (이메일을 Key, 이름을 Value)
                }
            }
            log.info("Loaded {} members into whitelist", whitelist.size());
            // 맵에 실제로 저장된 Key-Value 쌍 전체를 출력
            log.info("Whitelist contents: {}", whitelist);

        }catch(IOException e){
            log.error("Failed to load whitelist", e);
        }
    }

    //구글 정보와 whitelist 일치하는지 검증
    public boolean isMember(String googleName, String googleEmail) {
        //whitelist에 이메일 키 있는지 검증
        if(!whitelist.containsKey(googleEmail)){
            log.warn("User {} does not have a whitelisted member. (Email NOT FOUND)", googleEmail);
            return false;
        }

        //이름 비교
        String whitelistName = whitelist.get(googleEmail);
        boolean isMatch = googleName.equals(whitelistName);
        if(!isMatch){
            log.info("Name mismatch (but allowing login): Google='{}', Whitelist='{}'", googleName, whitelistName);
        }
        return true;
    }

}


