package com.example.lionsforest.domain.user.repository;

import com.example.lionsforest.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
