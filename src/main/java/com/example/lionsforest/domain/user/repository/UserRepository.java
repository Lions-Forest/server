package com.example.lionsforest.domain.user.repository;

import com.example.lionsforest.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 찾기 - oauth 로그인에 사용
    Optional<User> findByEmail(String email);

    // 닉네임 존재하는지 확인 - 닉네임 수정 시 사용(중복검사)
    boolean existsByNickname(String nickname);
}
