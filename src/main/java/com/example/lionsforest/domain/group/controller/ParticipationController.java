package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import com.example.lionsforest.domain.group.service.ParticipationService;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ParticipationResponseDto> joinGroup(@PathVariable("group_id") Long groupId,
                                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(participationService.joinGroup(groupId, loginUserId));
    }

    // 모임 탈퇴
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> leaveGroup(@PathVariable("group_id") Long groupId,
                                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Long loginUserId = Long.valueOf(principal.getUsername());

        participationService.leaveGroup(groupId, loginUserId);
        return ResponseEntity.ok("모임에서 탈퇴했습니다.");
    }

    // 모임 참여자 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<ParticipationResponseDto>> getUser(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(participationService.getParticipationsByGroupId(groupId));
    }
}
