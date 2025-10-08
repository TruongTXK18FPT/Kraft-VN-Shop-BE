package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
