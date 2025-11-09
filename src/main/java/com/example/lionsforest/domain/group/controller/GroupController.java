package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.GroupDeleteRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetDetailResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.service.GroupService;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    // 모임 개설
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupResponseDto> createGroup(@RequestPart("dto") GroupRequestDto dto,
                                                        @RequestPart(value = "photos", required = false) List<MultipartFile> photos){
        User user = userRepository.findById(dto.getUserId()) // 임시 인증(JWT 이전)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */

        GroupResponseDto responseDto = groupService.createGroup(dto, photos, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 모임 정보 전체 조회(썸네일 포함)
    @GetMapping
    public ResponseEntity<List<GroupGetResponseDto>> getAllGroups(){
        return ResponseEntity.ok(groupService.getAllGroup());
    }

    // 모임 정보 상세 조회
    @GetMapping("/{group_id}")
    public ResponseEntity<GroupGetDetailResponseDto> getGroupByID(@PathVariable Long groupId){
        GroupGetDetailResponseDto responseDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(responseDto);
    }

    // 모임 정보 수정
    @PatchMapping("/{group_id}")
    public ResponseEntity<GroupResponseDto> updateGroup(@PathVariable Long groupId,
                                                        @RequestBody GroupUpdateRequestDto dto){
        User user = userRepository.findById(dto.getUserId()) // 임시 인증(JWT 이전)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */
        return ResponseEntity.ok(groupService.updateGroup(groupId, dto, user));
    }

    // 모임 삭제
    @DeleteMapping("/{group_id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupId,
                                              @RequestBody GroupDeleteRequestDto dto){
        User user = userRepository.findById(dto.getUserId()) // 임시 인증(JWT 이전)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */

        groupService.deleteGroup(groupId, user);

        return ResponseEntity.ok("모임이 성공적으로 삭제되었습니다.");
    }

    // 모임 사진 일괄 수정 (추가 + 삭제)
    @PostMapping(value = "/{groupId}/photos/manage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> manageGroupPhotos(
            @PathVariable Long groupId,
            // 새로 추가할 파일 목록
            @RequestPart(value = "addPhotos", required = false) List<MultipartFile> addPhotos,
            // 삭제할 사진 ID 목록 (예: ?deletePhotoIds=1&deletePhotoIds=3)
            @RequestPart(value = "deletePhotoIds", required = false) List<Long> deletePhotoIds,
            @RequestPart("userId") Long userId // [임시 인증]
    ) {
        // [임시 인증]
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        /* 파라미터에 @AuthenticationPrincipal UserDetailsImpl userDetails 추가
        User user = userDetails.getUser(); // 진짜 인증(JWT) */

        // 서비스의 새 메서드 호출
        groupService.managePhotos(groupId, addPhotos, deletePhotoIds, user);

        return ResponseEntity.ok("사진이 성공적으로 수정되었습니다.");
    }
}
