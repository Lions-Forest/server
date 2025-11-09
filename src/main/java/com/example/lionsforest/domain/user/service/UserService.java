package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.UserInfoResponseDTO;
import com.example.lionsforest.domain.user.dto.UserUpdateRequestDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용
public class UserService {

    private final UserRepository userRepository;

    //유저 목록 전체 조회
    public List<UserInfoResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserInfoResponseDTO::from) // 메서드 참조
                .collect(Collectors.toList());
    }

    //유저 정보 상세 조회
    public UserInfoResponseDTO getUserInfo(Long userId) {
        User user = findUserById(userId);
        return UserInfoResponseDTO.from(user);
    }

    // 유저 정보 수정
    @Transactional
    public UserInfoResponseDTO updateUserInfo(Long userId, UserUpdateRequestDTO request) {
        User user = findUserById(userId);

        // 닉네임 중복 검사 (변경 시에만)
        if (request.getNickname() != null &&
                !request.getNickname().equals(user.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // User 엔티티 내부의 update 메서드 호출 (JPA 변경 감지)
        user.updateProfile(
                request.getNickname(),
                request.getBio(),
                request.getProfile_photo()
        );

        return UserInfoResponseDTO.from(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userId));
    }
}