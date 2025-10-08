package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findAllByRoleAndActiveIsNotNullOrderByCreatedAtDesc(com.mss301.kraft.common.enums.Role role);
}
