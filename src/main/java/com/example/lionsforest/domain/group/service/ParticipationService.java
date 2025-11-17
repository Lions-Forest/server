package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.group.dto.response.GroupGetResponseDto;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;

import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupPhotoRepository groupPhotoRepository;
    private final NotificationRepository notificationRepository;

    // 모임 참여
    @Transactional
    public ParticipationResponseDto joinGroup(Long groupId, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 중복 참여 체크
        if(participationRepository.existsByGroupAndUser(group, user)) {
            throw new BusinessException(ErrorCode.PARTICIPATION_ALREADY_EXISTS);
        }

        // 인원 제한 체크
        long currentCount = participationRepository.countByGroupId(groupId);
        int capacity = group.getCapacity();
        if(currentCount >= capacity) {
            throw new BusinessException(ErrorCode.GROUP_CAPACITY_FULL);
        }

        Participation participation = Participation.builder()
                .group(group)
                .user(user)
                .build();

        Participation saved = participationRepository.save(participation);

        // 알림 생성: 본인에게 참여 확정 알림 보내기
        String dateStr = group.getMeetingAt().format(DateTimeFormatter.ofPattern("yy.MM.dd"));
        String content = "✅ [" + dateStr + "] " + group.getTitle() + " 모임에 참여가 확정되었어요!";
        // 모임 대표 사진 경로 가져오기
        String photoPath = null;
        Optional<GroupPhoto> firstPhotoOpt = groupPhotoRepository.findFirstByGroupIdOrderByPhotoOrderAsc(groupId);
        if (firstPhotoOpt.isPresent()) {
            photoPath = firstPhotoOpt.get().getPhoto();
        }
        Notification notification = Notification.builder()
                .user(user)  // 본인에게
                .content(content)
                .photo(photoPath)
                .build();
        notificationRepository.save(notification);

        long after = currentCount + 1;
        if (after >= capacity && group.getState() != GroupState.CLOSED) {
            group.setState(GroupState.CLOSED); // 모집완료로 전환
        }

        return ParticipationResponseDto.fromEntity(saved);
    }

    // 내가 참여한 모임 목록 조회
    @Transactional(readOnly = true)
    public List<GroupGetResponseDto> getAllMyParticipations(Long userId){
        List<Participation> participations = participationRepository.findByUserId(userId);

        return participations.stream()
                .map(participation -> GroupGetResponseDto.fromEntity(participation.getGroup()))
                .toList();
    }

    // 모임 탈퇴
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (group.getLeader().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.GROUP_LEADER_CANNOT_LEAVE);
        }

        Participation participation = participationRepository
                .findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_PARTICIPATION_NOT_FOUND));

        participationRepository.delete(participation);

        long after = participationRepository.countByGroupId(groupId);
        if (after < group.getCapacity() && group.getState() == GroupState.CLOSED) {
            group.setState(GroupState.OPEN); // 다시 모집중
        }
    }

    // 모임 참여자 조회
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getParticipationsByGroupId(Long groupId){
        List<Participation> participations = participationRepository.findByGroupId(groupId);

        return participations.stream()
                .map(ParticipationResponseDto::fromEntity)
                .toList();
    }
}
