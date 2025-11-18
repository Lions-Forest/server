package com.example.lionsforest.domain.group.controller;

import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupSimpleInfoResponseDto;
import com.example.lionsforest.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups/")
@Tag(name = "모임", description = "모임 관련 API")
public class GroupController {

    private final GroupService groupService;

    // 모임 개설
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모임 개설", description =  """
        요청 형식: multipart/form-data
        - title: string
        - category: MEAL(식사) | WORK(모각작) | CAFE(카페) | SOCIAL(소모임) | CULTURE(문화예술) | ETC(기타)
        - capacity: int (2~50)
        - meetingAt: ISO-8601 (예: 2025-11-15T14:00:00)
        - location: string
        - photos: 이미지 파일 여러 개 (동일 키 'photos'로 append)
        
             ### 💻 프론트 전송 예시 (Axios)
                     ```javascript
                     const form = new FormData();
                     form.append("title", "주말 등산 모임");
                     form.append("category", "MEAL");
                     form.append("capacity", "10");
                     form.append("meetingAt", "2025-11-15T14:00:00");
                     form.append("location", "서울 북한산 입구");
                     files.forEach(f => form.append("photos", f)); // 동일 키로 여러 번 append
            
                     await axios.post("/api/groups/", form, {
                       headers: { "Content-Type": "multipart/form-data" }
                     });
            
        """)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(   // Swagger 문서화용
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = GroupRequestDto.class)
            ))
    public ResponseEntity<GroupResponseDto> createGroup(@ModelAttribute GroupRequestDto req,
                                                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){

        Long loginUserId = Long.valueOf(principal.getUsername());

        GroupResponseDto responseDto = groupService.createGroup(req, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 모임 정보 전체 조회
    @GetMapping
    @Operation(summary = "모임 정보 전체 조회", description = "개설된 모임을 모두 조회합니다")
    public ResponseEntity<List<GroupGetResponseDto>> getAllGroups(){
        return ResponseEntity.ok(groupService.getAllGroup());
    }

    // 모임 정보 상세 조회
    @GetMapping("{group_id}/")
    @Operation(summary = "모임 정보 상세 조회", description = "특정 모임(By group_id)에 대한 정보를 조회합니다")
    public ResponseEntity<GroupGetResponseDto> getGroupByID(@PathVariable("group_id") Long groupId){
        GroupGetResponseDto responseDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(responseDto);
    }

    // 내가 개설한 모임 전체 조회
    @GetMapping("leader/")
    @Operation(summary = "내가 개설한 모임 전체 조회", description = "내가 개설한 모임에 대한 정보를 조회합니다")
    public ResponseEntity<List<GroupGetResponseDto>> getAllGroupByLeader(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        List<GroupGetResponseDto> responseDto = groupService.getAllGroupByLeader(loginUserId);
        return ResponseEntity.ok(responseDto);
    }

    // 모임 정보 수정
    @PatchMapping("{group_id}/")
    @Operation(summary = "모임 정보 수정", description = "특정 모임(By group_id)의 정보를 수정합니다(사진 제외)")
    public ResponseEntity<GroupResponseDto> updateGroup(@PathVariable("group_id") Long groupId,
                                                        @RequestBody GroupUpdateRequestDto dto,
                                                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        return ResponseEntity.ok(groupService.updateGroup(groupId, dto, loginUserId));
    }

    // 모임 삭제
    @DeleteMapping("{group_id}/")
    @Operation(summary = "모임 삭제", description = "특정 모임(By group_id)을 삭제합니다")
    public ResponseEntity<String> deleteGroup(@PathVariable("group_id") Long groupId,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal){
        Long loginUserId = Long.valueOf(principal.getUsername());

        groupService.deleteGroup(groupId, loginUserId);

        return ResponseEntity.ok("모임이 성공적으로 삭제되었습니다.");
    }

    //모임 정보 간단 조회
    @GetMapping("{group_id}/simple")
    @Operation(summary = "모임 정보 간단 조회", description = "후기 작성 시 상단에 표시할 특정 모임의 정보를 반환합니다.")
    public ResponseEntity<GroupSimpleInfoResponseDto> getGroupSimpleInfo(@PathVariable("group_id") Long groupId){
        GroupSimpleInfoResponseDto response = groupService.getGroupSimpleInfo(groupId);
        return ResponseEntity.ok(response);
    }

}
