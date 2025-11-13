package com.example.lionsforest.domain.user.service;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.dto.UserInfoResponseDTO;
import com.example.lionsforest.domain.user.dto.UserUpdateRequestDTO;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.common.S3UploadService;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용
public class UserService {

    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

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
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 닉네임, 한 줄 소개 업데이트
        user.updateNicknameAndBio(request.getNickname(), request.getBio());

        // photo S3에 업로드 - 요청받은 photo가 존재할 때만
        MultipartFile photo = request.getPhoto();
        boolean removePhotoFlag = (request.getRemovePhoto() != null && request.getRemovePhoto());

        //사진 제거하는 경우
        if(removePhotoFlag) {
            //기존 사진이 있으면 S3에서 삭제
            if(user.getProfile_photo() != null) {
                s3UploadService.delete(user.getProfile_photo());
            }
            //DB에도 null로 설정
            user.setProfile_photo(null);
        }
        //제거 요청 X, 새 사진 업로드
        else if(photo != null) {
            //기존 사진 있으면 s3에서 삭제
            if(user.getProfile_photo() != null) {
                s3UploadService.delete(user.getProfile_photo());
            }
            String newPhotoUrl = s3UploadService.upload(photo, "profile_photo");
            //DB에 새 photoUrl 저장
            user.setProfile_photo(newPhotoUrl);
        }

        return UserInfoResponseDTO.from(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}