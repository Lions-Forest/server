package com.example.lionsforest.domain.user.controller;


import com.example.lionsforest.domain.user.dto.response.NicknameResponseDTO;
import com.example.lionsforest.domain.user.dto.response.UserInfoResponseDTO;
import com.example.lionsforest.domain.user.dto.request.UserUpdateRequestDTO;
import com.example.lionsforest.domain.user.service.NicknameService;
import com.example.lionsforest.domain.user.service.UserService;
import com.example.lionsforest.global.config.PrincipalHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final NicknameService nicknameService;

    //유저 목록 조회
    @GetMapping
    @Operation(summary = "유저 목록 조회", description = "전체 유저 목록을 조회합니다")
    public ResponseEntity<List<UserInfoResponseDTO>> getUserList() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //유저 상세 조회
    @GetMapping("/{userId}")
    @Operation(summary = "유저 상세 조회", description = "특정 유저의 상세 정보를 조회합니다(By user_id)")
    public ResponseEntity<UserInfoResponseDTO> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    //내 정보 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "마이페이지에서 내 상세 정보를 조회합니다")
    public ResponseEntity<UserInfoResponseDTO> getMyInfo() {
        Long authenticatedUserId = PrincipalHandler.getUserId();
        UserInfoResponseDTO response = userService.getUserInfo(authenticatedUserId);
        return ResponseEntity.ok(response);
    }

    //내 정보 수정
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 정보 수정", description = "마이페이지에서 내 유저 정보를 수정합니다")
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = UserUpdateRequestDTO.class)
            )
    )
    public ResponseEntity<UserInfoResponseDTO> updateUser(
            @ModelAttribute @Valid UserUpdateRequestDTO request) {
        Long authenticatedUserId = PrincipalHandler.getUserId();

        UserInfoResponseDTO updatedUser = userService.updateUserInfo(authenticatedUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    //랜덤 닉네임 생성
    @GetMapping("/me/random-nickname")
    @Operation(summary = "랜덤 닉네임 생성", description = "마이페이지에서 랜덤 닉네임을 생성합니다")
    public ResponseEntity<NicknameResponseDTO> createNickname(){
        Long authenticatedUserId = PrincipalHandler.getUserId();
        NicknameResponseDTO createdNickname = nicknameService.updateRandomNickname(authenticatedUserId);
        return ResponseEntity.ok(createdNickname);
    }
}