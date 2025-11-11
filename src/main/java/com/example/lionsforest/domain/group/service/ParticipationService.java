package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;

import com.example.lionsforest.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // 모임 참여
    @Transactional
    public ParticipationResponseDto joinGroup(Long groupId, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 중복 참여 체크
        if(participationRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("이미 참여 신청한 모임입니다.");
        }

        // 인원 제한 체크
        long currentCount = participationRepository.countByGroupId(groupId);
        int capacity = group.getCapacity();
        if(currentCount >= capacity) {
            throw new IllegalArgumentException("모임 인원이 가득 찼습니다.");
        }

        Participation participation = Participation.builder()
                .group(group)
                .user(user)
                .build();

        Participation saved = participationRepository.save(participation);

        long after = currentCount + 1;
        if (after >= capacity && group.getState() != GroupState.CLOSED) {
            group.setState(GroupState.CLOSED); // 모집완료로 전환
        }

        return ParticipationResponseDto.fromEntity(saved);
    }

    // 내가 참여한 모임 목록 조회
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getAllMyParticipations(Long userId){
        List<Participation> participations = participationRepository.findByUserId(userId);

        return participations.stream()
                .map(ParticipationResponseDto::fromEntity)
                .toList();
    }

    // 모임 탈퇴
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Participation participation = participationRepository
                .findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("참여하지 않은 모임입니다."));

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
