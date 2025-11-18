package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.response.NicknameResponseDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NicknameService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    //닉네임 구성 후보 파일(json) 로드
    @Value("classpath:nickname-components.json")
    private Resource nicknameResource;

    //파일에서 읽어온 형용사, 명사 리스트 저장
    private List<String> adjectives;
    private List<String> nouns;

    private record NicknameData(
            List<String> adj,
            List<String> noun
    ){}

    //서버 시작할 때 한 번만 실행되는 로직 - 파일 읽어와서 저장함
    @PostConstruct
    public void loadNicknameComponents() {
        try(InputStream inputStream = nicknameResource.getInputStream()){
            NicknameData data = objectMapper.readValue(inputStream, NicknameData.class);
            this.adjectives = data.adj();
            this.nouns = data.noun();
        }catch(IOException e){
            throw new RuntimeException("Failed to load nickname components", e);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    //닉네임 랜덤 생성 로직
    public String generateRandomNickname(String excludeNickname) {
        //maxTries 만큼 시도
        int maxTries = 20;
        for(int i = 0; i < maxTries; i++){
            //랜덤 형용사+명사 조합으로 새 닉네임 생성
            String newAdj = adjectives.get(random.nextInt(adjectives.size()));
            String newNoun = nouns.get(random.nextInt(nouns.size()));
            String candidate = newAdj + " " + newNoun;
            //중복 검사 통과하면 새로운 닉네임 반환
            //1)내 기존 닉네임과 다름 2) 다른 사용자 닉네임과 다름
            if(!excludeNickname.equals(candidate) && !userRepository.existsByNickname(candidate)){
                return candidate;
            }
        }
        //검사 통과 못하면 예외처리
        throw new BusinessException(ErrorCode.NICKNAME_GENERATION_FAILED);

    }
    @Transactional
    public NicknameResponseDTO updateRandomNickname(Long userId) {
        User user = findUserById(userId);
        String oldNickname = user.getNickname();
        String newNickname = generateRandomNickname(oldNickname);
        user.setNickname(newNickname);


        return NicknameResponseDTO.builder()
                .nickname(newNickname).build();

    }
}
