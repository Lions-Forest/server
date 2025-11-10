package com.example.lionsforest.domain.user.controller;


import com.example.lionsforest.domain.user.dto.UserInfoResponseDTO;
import com.example.lionsforest.domain.user.dto.UserUpdateRequestDTO;
import com.example.lionsforest.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //유저 목록 조회
    @GetMapping
    public ResponseEntity<List<UserInfoResponseDTO>> getUserList() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //유저 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDTO> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    //유저 정보 수정
    @PatchMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDTO request) {


        UserInfoResponseDTO updatedUser = userService.updateUserInfo(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}