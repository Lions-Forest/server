package com.example.lionsforest.domain.group.repository;

import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByGroupAndUser(Group group, User user);

    long countByGroupId(Long groupId);

    Optional<Participation> findByGroupIdAndUserId(Long groupId, Long UserId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<Participation> findByGroupId(Long groupId);
    List<Participation> findByUserId(Long userId);
}
