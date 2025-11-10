package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.user.User;


import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.common.S3UploadService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupPhotoRepository groupPhotoRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 모임 개설
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto dto,
                                        List<MultipartFile> photos,
                                        Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // Group Entity 먼저 생성(ID 확보)
        Group group = dto.toEntity(user);
        Group saved = groupRepository.save(group);

        if (photos != null && !photos.isEmpty()) {
            List<GroupPhoto> groupPhotos = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile photo = photos.get(i);

                // S3에 파일 업로드 -> URL 반환
                String photoUrl = s3UploadService.upload(photo, "group-photos");
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
        }

        return new GroupResponseDto(saved.getId(),
                saved.getTitle(), saved.getCategory(),
                saved.getCapacity(), saved.getMeetingAt(),
                saved.getLocation(), saved.getState());
    }

    // 모임 정보 전체 조회
    @Transactional(readOnly = true)
    public List<GroupGetResponseDto> getAllGroup(){
        return groupRepository.findAll().stream()
                .map(GroupGetResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    // 모임 정보 상세 조회
    @Transactional(readOnly = true)
    public GroupGetResponseDto getGroupById(Long groupId) {

        Group group = groupRepository.findByIdWithPhotos(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        return GroupGetResponseDto.fromEntity(group);
    }

    // 모임 수정
    @Transactional
    public GroupResponseDto updateGroup(Long groupId, GroupUpdateRequestDto dto, Long userId){

        // 모임 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 유저 권한 확인
        if(!group.getLeader().getId().equals(user.getId())){
            throw new IllegalArgumentException("모임장만 모임을 수정할 수 있습니다.");
        }

        if (dto.getTitle() != null) {
            group.setTitle(dto.getTitle());
        }
        if (dto.getCategory() != null) {
            group.setCategory(dto.getCategory());
        }
        if (dto.getCapacity() != null) {
            group.setCapacity(dto.getCapacity());
        }
        if (dto.getMeetingAt() != null) {
            group.setMeetingAt(dto.getMeetingAt());
        }
        if (dto.getLocation() != null) {
            group.setLocation(dto.getLocation());
        }
        if (dto.getState() != null) {
            group.setState(dto.getState());
        }

        return GroupResponseDto.fromEntity(group);
    }

    // 모임 삭제
    @Transactional
    public void deleteGroup(Long groupId, Long userId){
        // 모임 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 유저 권한 확인
        if(!group.getLeader().getId().equals(user.getId())){
            throw new IllegalArgumentException("모임장만 모임을 수정할 수 있습니다.");
        }

        // 사진 삭제
        if (group.getPhotos() != null) {
            group.getPhotos().forEach(p -> s3UploadService.delete(p.getPhoto()));
        }

        //삭제
        groupRepository.delete(group);
    }

    /*
    //사진 일괄 관리 (추가 + 삭제)
    @Transactional
    public void managePhotos(Long groupId, List<MultipartFile> addPhotos, List<Long> deletePhotoIds, User user) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        if (!group.getLeader().getId().equals(user.getId())) {
            throw new IllegalArgumentException("모임장만 사진을 수정할 수 있습니다.");
        }

        // 사진 갯수 제한 검증(최대 5장)
        // 현재 사진 개수
        int currentPhotoCount = groupPhotoRepository.countByGroupId(groupId); // (Repository에 countByGroupId 추가 필요)
        // 삭제할 개수
        int deleteCount = (deletePhotoIds != null) ? deletePhotoIds.size() : 0;
        // 추가할 개수
        int addCount = (addPhotos != null) ? addPhotos.size() : 0;
        // 최종 개수 확인
        if (currentPhotoCount - deleteCount + addCount > 5) {
            throw new IllegalArgumentException("사진은 최대 5개까지만 업로드할 수 있습니다.");
        }

        // 사진 삭제 (DB + 로컬/S3 파일 삭제)
        if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
            List<GroupPhoto> photosToDelete = groupPhotoRepository.findAllById(deletePhotoIds);

            for (GroupPhoto photo : photosToDelete) {
                if (!photo.getGroup().getId().equals(groupId)) {
                    throw new IllegalArgumentException("다른 모임의 사진을 삭제할 수 없습니다.");
                }
                s3UploadService.delete(photo.getPhoto());
            }
            groupPhotoRepository.deleteAll(photosToDelete);
        }

        // 사진 추가 (로컬/S3 업로드 + DB 저장)
        if (addPhotos != null && !addPhotos.isEmpty()) {
            // (createGroup의 사진 추가 로직과 동일)
            List<GroupPhoto> groupPhotos = new ArrayList<>();
            // (참고: photo_order는 이 로직에서 관리하기 매우 복잡해집니다)
            for (int i = 0; i < addPhotos.size(); i++) {
                String photoUrl = s3UploadService.upload(addPhotos.get(i), "group-photos");
                groupPhotos.add(GroupPhoto.builder()
                        .group(group)
                        .photo(photoUrl)
                        .photo_order(i + 100) // (순서 로직은 별도 정책 필요)
                        .build());
            }
            groupPhotoRepository.saveAll(groupPhotos);
        }
    }
    */

}
