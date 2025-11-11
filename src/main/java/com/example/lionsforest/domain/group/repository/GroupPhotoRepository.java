package com.example.lionsforest.domain.group.repository;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPhotoRepository extends JpaRepository<GroupPhoto, Long> {
    List<GroupPhoto> findAllByGroup(Group group);
    int countByGroupId(Long groupId);
}
