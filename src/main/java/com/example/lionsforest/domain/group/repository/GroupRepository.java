package com.example.lionsforest.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.lionsforest.domain.group.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface GroupRepository extends JpaRepository<Group, Long> {
    // 상세 조회 시 N+1 문제를 피하기 위해 fetch join 사용
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.photos WHERE g.id = :id")
    Optional<Group> findByIdWithPhotos(@Param("id") Long id);

    List<Group> findAllByLeaderId(Long leaderId);

    List<Group> findByMeetingAtBetween(LocalDateTime startRange, LocalDateTime endRange);
}
