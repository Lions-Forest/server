package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.service.GroupService;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Tag(name = "모임", description = "모임 관련 API")
public class GroupController {

    private final GroupService groupService;

    // 모임 개설
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모임 개설", description = "모임을 개설합니다")
    public ResponseEntity<GroupResponseDto> createGroup(@RequestPart("dto") GroupRequestDto dto,
                                                        @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                                                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        GroupResponseDto responseDto = groupService.createGroup(dto, photos, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 모임 정보 전체 조회
    @GetMapping
    @Operation(summary = "모임 정보 전체 조회", description = "개설된 모임을 모두 조회합니다")
    public ResponseEntity<List<GroupGetResponseDto>> getAllGroups(){
        return ResponseEntity.ok(groupService.getAllGroup());
    }

    // 모임 정보 상세 조회
    @GetMapping("/{group_id}")
    @Operation(summary = "모임 정보 상세 조회", description = "특정 모임(By group_id)에 대한 정보를 조회합니다")
    public ResponseEntity<GroupGetResponseDto> getGroupByID(@PathVariable("group_id") Long groupId){
        GroupGetResponseDto responseDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(responseDto);
    }

    // 모임 정보 수정
    @PatchMapping("/{group_id}")
    @Operation(summary = "모임 정보 수정", description = "특정 모임(By group_id)의 정보를 수정합니다(사진 제외)")
    public ResponseEntity<GroupResponseDto> updateGroup(@PathVariable("group_id") Long groupId,
                                                        @RequestBody GroupUpdateRequestDto dto,
                                                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(groupService.updateGroup(groupId, dto, loginUserId));
    }

    // 모임 삭제
    @DeleteMapping("/{group_id}")
    @Operation(summary = "모임 삭제", description = "특정 모임(By group_id)을 삭제합니다")
    public ResponseEntity<String> deleteGroup(@PathVariable("group_id") Long groupId,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        groupService.deleteGroup(groupId, loginUserId);

        return ResponseEntity.ok("모임이 성공적으로 삭제되었습니다.");
    }
    /*
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
        User user = userDetails.getUser(); // 진짜 인증(JWT)

        // 서비스의 새 메서드 호출
        groupService.managePhotos(groupId, addPhotos, deletePhotoIds, user);

        return ResponseEntity.ok("사진이 성공적으로 수정되었습니다.");
    }
    */
}
