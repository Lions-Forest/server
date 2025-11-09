package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.dto.response.ParticipationResponseDto;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final GroupRepository groupRepository;

    // 모임 참여
    @Transactional
    public ParticipationResponseDto joinGroup(Long groupId, User user){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 중복 참여 체크
        if(participationRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("이미 참여 신청한 모임입니다.");
        }

        // 인원 제한 체크
        long currentCount = participationRepository.countByGroupId(groupId);
        if(currentCount >= group.getCapacity()) {
            throw new IllegalArgumentException("모임 인원이 가득 찼습니다.");
        }

        Participation participation = Participation.builder()
                .group(group)
                .user(user)
                .build();

        Participation saved = participationRepository.save(participation);
        return ParticipationResponseDto.fromEntity(saved);
    }

    // 모임 탈퇴
    @Transactional
    public void leaveGroup(Long groupId, User user) {
        Participation participation = participationRepository
                .findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("참여하지 않은 모임입니다."));

        participationRepository.delete(participation);
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
