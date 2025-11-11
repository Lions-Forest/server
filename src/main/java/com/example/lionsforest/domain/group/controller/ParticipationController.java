package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import com.example.lionsforest.domain.group.service.ParticipationService;

import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participation/")
@Tag(name = "모임 참여", description = "모임 참여 관련 API")
public class ParticipationController {
    private final ParticipationService participationService;
    private final UserRepository userRepository;

    // 모임 참여
    @PostMapping("{group_id}/")
    @Operation(summary = "모임 참여", description = "특정 모임(By group_id)에 참여합니다")
    public ResponseEntity<ParticipationResponseDto> joinGroup(@PathVariable("group_id") Long groupId,
                                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(participationService.joinGroup(groupId, loginUserId));
    }

    // 내가 참여한 모임 조회
    @GetMapping("my/")
    @Operation(summary = "내가 참여한 모임 조회", description = "내가 참여한 모임을 모두 조회합니다")
    public ResponseEntity<List<ParticipationResponseDto>> getUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(participationService.getAllMyParticipations(loginUserId));
    }

    // 모임 탈퇴
    @DeleteMapping("{group_id}/")
    @Operation(summary = "모임 탈퇴", description = "특정 모임(By group_id)에서 탈퇴합니다")
    public ResponseEntity<String> leaveGroup(@PathVariable("group_id") Long groupId,
                                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        Long loginUserId = Long.valueOf(principal.getUsername());

        participationService.leaveGroup(groupId, loginUserId);
        return ResponseEntity.ok("모임에서 탈퇴했습니다.");
    }

    // 모임 참여자 조회
    @GetMapping("{group_id}/")
    @Operation(summary = "모임 참여자 조회", description = "특정 모임(By group_id)에 참여자를 모두 조회합니다")
    public ResponseEntity<List<ParticipationResponseDto>> getUser(@PathVariable("group_id") Long groupId){
        return ResponseEntity.ok(participationService.getParticipationsByGroupId(groupId));
    }
}
