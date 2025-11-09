package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.ParticipationRequestDto;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.group.service.ParticipationService;

import com.example.lionsforest.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {
    private final ParticipationService participationService;

    // 모임 참여
    @PostMapping("/{group_id}")
    public ResponseEntity<ParticipationResponseDto> joinGroup(@PathVariable Long groupId,
                                                              @RequestBody ParticipationRequestDto dto){
        return ResponseEntity.ok(participationService.joinGroup(groupId, dto.getUserId()));
    }

    // 모임 탈퇴
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> leaveGroup(@PathVariable Long groupId,
                                             @RequestBody ParticipationRequestDto dto) {
        participationService.leaveGroup(groupId, dto.getUserId());
        return ResponseEntity.ok("모임에서 탈퇴했습니다.");
    }

    // 모임 참여자 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<List<ParticipationResponseDto>> getUser(@PathVariable Long groupId){
        return ResponseEntity.ok(participationService.getParticipationsByGroupId(groupId));
    }
}
