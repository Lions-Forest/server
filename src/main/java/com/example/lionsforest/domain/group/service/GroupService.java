package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.dto.request.GroupDeleteRequestDto;
import com.example.lionsforest.domain.group.dto.request.GroupRequestDto;
import com.example.lionsforest.domain.group.dto.response.GroupResponseDto;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.dto.request.GroupUpdateRequestDto;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;


    // 모임 개설
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto dto){
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Group group = dto.toEntity(user);
        Group saved = groupRepository.save(group);
        return new GroupResponseDto(saved.getId(),
                saved.getTitle(), saved.getCategory(),
                saved.getCount(), saved.getMeeting_at(),
                saved.getLocation(), saved.getState());
    }

    // 모임 정보 전체 조회
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


}
