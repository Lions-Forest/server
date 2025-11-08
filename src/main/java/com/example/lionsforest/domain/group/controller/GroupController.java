package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.GroupDeleteRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    // 모임 개설
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@RequestBody GroupRequestDto dto){
        return ResponseEntity.ok(groupService.createGroup(dto));
    }

    // 모임 정보 전체 조회
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> getAllGroups(){
        return ResponseEntity.ok(groupService.getAllGroup());
    }

    // 모임 정보 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroupByID(@PathVariable Long id){
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    // 모임 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(@PathVariable Long id,
                                                        @RequestBody GroupUpdateRequestDto dto){
        return ResponseEntity.ok(groupService.updateGroup(id, dto));
    }

    // 모임 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id,
                                              @RequestBody GroupDeleteRequestDto dto){
        return ResponseEntity.ok("모임이 성공적으로 삭제되었습니다.");
    }

}
