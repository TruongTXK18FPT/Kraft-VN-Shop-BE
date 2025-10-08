package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.user_service.entity.Address;
import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
}
