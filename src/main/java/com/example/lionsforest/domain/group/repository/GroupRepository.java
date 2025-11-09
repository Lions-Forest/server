package com.example.lionsforest.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.lionsforest.domain.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
