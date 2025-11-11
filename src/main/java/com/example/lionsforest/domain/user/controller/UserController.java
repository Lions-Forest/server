package com.example.lionsforest.domain.user.controller;


import com.example.lionsforest.domain.user.dto.UserInfoResponseDTO;
import com.example.lionsforest.domain.user.dto.UserUpdateRequestDTO;
import com.example.lionsforest.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

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

    //유저 정보 수정
    @PatchMapping("/{userId}")
    @Operation(summary = "유저 정보 수정", description = "user_id 유저의 정보를 수정합니다")
    public ResponseEntity<UserInfoResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDTO request) {


        UserInfoResponseDTO updatedUser = userService.updateUserInfo(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}