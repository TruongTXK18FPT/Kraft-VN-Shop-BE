package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
    // Dashboard analytics methods
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt < :date")
    Long countByCreatedAtBefore(@Param("date") OffsetDateTime date);
}
