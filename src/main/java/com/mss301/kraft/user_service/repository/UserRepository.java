package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findAllByRoleAndActiveIsNotNullOrderByCreatedAtDesc(com.mss301.kraft.common.enums.Role role);

    @Query(value = "SELECT u FROM User u WHERE u.role = :role AND u.active IS NOT NULL ORDER BY u.createdAt DESC", countQuery = "SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active IS NOT NULL")
    Page<User> findAllByRolePaged(Role role, Pageable pageable);

    // Dashboard analytics methods
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt < :date")
    Long countByCreatedAtBefore(@Param("date") OffsetDateTime date);
}
