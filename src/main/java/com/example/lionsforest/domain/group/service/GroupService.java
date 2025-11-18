package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.dto.response.GroupSimpleInfoResponseDto;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import com.example.lionsforest.domain.user.User;


import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.common.S3UploadService;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupPhotoRepository groupPhotoRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final ParticipationRepository participationRepository;
    private final NotificationRepository notificationRepository;

    // 모임 개설
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto dto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 모임 시각 검증 - 현재 시각 넘지 않는지
        if(dto.getMeetingAt().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.GROUP_CREATION_TIME_EXCEEDED);
        }

        // Group Entity 먼저 생성(ID 확보)
        Group group = dto.toEntity(user);
        Group saved = groupRepository.save(group);

        List<MultipartFile> photos = dto.getPhotos();
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
                        .photoOrder(i)      // 사진 순서 (0부터 시작)
                        .build();

                groupPhotos.add(groupPhoto);
            }
            // GroupPhoto 리스트를 DB에 한 번에 저장 (Batch Insert)
            groupPhotoRepository.saveAll(groupPhotos);
        }

        // 모임장은 모임 자동 참여
        Participation leaderParticipation = Participation.builder()
                .group(saved)
                .user(user)
                .build();
        participationRepository.save(leaderParticipation);

        long participantCount = participationRepository.countByGroupId(saved.getId());
        GroupResponseDto response = GroupResponseDto.fromEntity(saved);
        response.setParticipantCount(participantCount);
        return response;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        return GroupGetResponseDto.fromEntity(group);
    }

    // 내가 개설한 모임 전체 조회
    @Transactional(readOnly = true)
    public List<GroupGetResponseDto> getAllGroupByLeader(Long userId){
        return groupRepository.findAllByLeaderId(userId).stream()
                .map(GroupGetResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 모임 수정
    @Transactional
    public GroupResponseDto updateGroup(Long groupId, GroupUpdateRequestDto dto, Long userId){

        // 모임 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저 권한 확인
        if(!group.getLeader().getId().equals(user.getId())){
            throw new BusinessException(ErrorCode.GROUP_PERMISSION_DENIED);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 모임 취소 시점 제한
        if (group.getMeetingAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.GROUP_CANCEL_TIME_EXCEEDED);
        }

        // 유저 권한 확인
        if(!group.getLeader().getId().equals(user.getId())){
            throw new BusinessException(ErrorCode.GROUP_PERMISSION_DENIED);
        }

        // 알림 생성: 모임 참가자들에게 모임 취소 알림 보내기
        // 해당 모임의 모든 참여 관계 조회 (모임장 제외)
        List<Participation> participations = participationRepository.findByGroupId(groupId);
        // 모임 첫 사진 가져오기
        String photoPath = null;
        Optional<GroupPhoto> firstPhotoOpt = groupPhotoRepository.findFirstByGroupIdOrderByPhotoOrderAsc(groupId);
        if (firstPhotoOpt.isPresent()) {
            photoPath = firstPhotoOpt.get().getPhoto();
        }
        // 알림 내용 구성 (예: 😢 "[yy.MM.dd] 모임제목" 모임이 취소되었습니다.)
        String dateStr = group.getMeetingAt().format(DateTimeFormatter.ofPattern("yy.MM.dd"));
        String content = "😢 ["+ dateStr + "] " + group.getTitle() + " 모임이 취소되었습니다.";
        for (Participation part : participations) {
            // 모임장을 제외하고 알림 전송 (모임장 본인은 알림 생략 가능)
            if (!part.getUser().getId().equals(userId)) {
                Notification notification = Notification.builder()
                        .user(part.getUser())
                        .content(content)
                        .photo(photoPath)
                        .build();
                notificationRepository.save(notification);
            }
        }


        // 사진 삭제
        if (group.getPhotos() != null) {
            group.getPhotos().forEach(p -> s3UploadService.delete(p.getPhoto()));
        }

        //삭제
        groupRepository.delete(group);
    }

    //시간 지난 모임 state 변경
    @Transactional
    public void closeExpiredMeetings(){
        int updatedCount = groupRepository.closeMeetingByTime(LocalDateTime.now());

        if(updatedCount > 0){
            System.out.println(updatedCount + "개의 모임이 마감 처리되었습니다.");
        }
    }

    //모임 간단 정보 조회
    public GroupSimpleInfoResponseDto getGroupSimpleInfo(Long groupId){
        Group group = groupRepository.findByIdWithPhotos(groupId)
                .orElseThrow(()->new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        return GroupSimpleInfoResponseDto.fromEntity(group);
    }
}
