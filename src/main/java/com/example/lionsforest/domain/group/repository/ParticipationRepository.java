package com.example.lionsforest.domain.group.repository;

import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByGroupAndUser(Group group, User user);

    //long countByGroupAndStatus(Group group, Participation);

    Optional<Participation> findByGroupIdAndUserId(Long groupId, Long UserId);

    List<Participation> findByGroupId(Long groupId);
    List<Participation> findByUserId(Long userId);
}
