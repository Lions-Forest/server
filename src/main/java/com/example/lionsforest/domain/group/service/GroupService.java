package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.dto.request.GroupDeleteRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.group.service.LocalUploadService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupPhotoRepository groupPhotoRepository; // (의존성 주입)
    private final LocalUploadService s3UploadService; // 파일업로드

    // 모임 개설
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto dto,
                                        List<MultipartFile> photos,
                                        User user){
        // Group Entity 먼저 생성(ID 확보)
        Group group = dto.toEntity(user);
        Group saved = groupRepository.save(group);

        if (photos != null && !photos.isEmpty()) {
            List<GroupPhoto> groupPhotos = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile photo = photos.get(i);

                // S3(또는 로컬)에 파일 업로드 -> URL 반환
                String photoUrl = s3UploadService.upload(photo, "s3폴더경로");
                // GroupPhoto 엔티티 생성
                GroupPhoto groupPhoto = GroupPhoto.builder()
                        .group(saved)        // 저장된 Group 객체
                        .photo(photoUrl)     // S3에서 반환된 URL
                        .photo_order(i)      // 사진 순서 (0부터 시작)
                        .build();

                groupPhotos.add(groupPhoto);
            }
            // GroupPhoto 리스트를 DB에 한 번에 저장 (Batch Insert)
            groupPhotoRepository.saveAll(groupPhotos);

        return new GroupResponseDto(saved.getId(),
                saved.getTitle(), saved.getCategory(),
                saved.getCapacity(), saved.getMeetingAt(),
                saved.getLocation(), saved.getState());
    }

/*    // 모임 정보 전체 조회
    public List<GroupResponseDto> getAllGroup(){
        return groupRepository.findAll().stream()
                .map(GroupResponseDto::fromEntity)
                .toList();
    }


    // 모임 정보 상세 조회
    public GroupResponseDto getGroupById(Long id) {
        Group product = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));
        return GroupResponseDto.fromEntity(product);
    }

    // 모임 수정
    @Transactional
    public GroupResponseDto updateGroup(Long groupId, GroupUpdateRequestDto dto){

        // 유저 조회
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 모임 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 유저 권한 확인
        if(!group.getLeader().equals(user.getId())){
            throw new IllegalArgumentException("모임장만 모임을 수정할 수 있습니다.");
        }

        // 모임 정보 수정
        group.update(dto.getTitle(), dto.getCategory(), dto.getCount(), dto.getMeeting_at(), dto.getLocation(), dto.getState());

        return GroupResponseDto.fromEntity(group);
    }

    // 모임 삭제
    @Transactional
    public void deleteGroup(Long groupId, GroupDeleteRequestDto dto){

        // 유저 조회
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 모임 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 유저 권한 확인
        if(!group.getLeader().equals(user.getId())){
            throw new IllegalArgumentException("모임장만 모임을 수정할 수 있습니다.");
        }

        //삭제
        groupRepository.delete(group);
    }

*/
}
