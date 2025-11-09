package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.ParticipationRequestDto;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.group.service.ParticipationService;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participation")
public class ParticipationController {
    private final ParticipationService participationService;
    private final UserRepository userRepository;

    // 모임 참여
    @PostMapping("/{group_id}")
    public ResponseEntity<ParticipationResponseDto> joinGroup(@PathVariable Long groupId,
                                                              @RequestBody ParticipationRequestDto dto){
        User user = userRepository.findById(dto.getUserId()) // 임시 인증(JWT 이전)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */

        return ResponseEntity.ok(participationService.joinGroup(groupId, user));
    }

    // 모임 탈퇴
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> leaveGroup(@PathVariable Long groupId,
                                             @RequestBody ParticipationRequestDto dto) {
        User user = userRepository.findById(dto.getUserId()) // 임시 인증(JWT 이전)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */

        participationService.leaveGroup(groupId, user);
        return ResponseEntity.ok("모임에서 탈퇴했습니다.");
    }

    // 모임 참여자 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<ParticipationResponseDto>> getUser(@PathVariable Long groupId){
        return ResponseEntity.ok(participationService.getParticipationsByGroupId(groupId));
    }
}
