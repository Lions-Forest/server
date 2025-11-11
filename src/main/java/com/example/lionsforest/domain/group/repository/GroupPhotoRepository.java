package com.example.lionsforest.domain.group.repository;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupPhotoRepository extends JpaRepository<GroupPhoto, Long> {
    List<GroupPhoto> findAllByGroup(Group group);
    int countByGroupId(Long groupId);

    // 특정 모임의 첫 사진 가져오기
    Optional<GroupPhoto> findFirstByGroupIdOrderByPhotoOrderAsc(Long groupId);
}
