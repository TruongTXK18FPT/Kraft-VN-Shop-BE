package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.UserAdminDtos.ToggleActiveRequest;
import com.mss301.kraft.admin_service.dto.UserAdminDtos.UserSummary;
import com.mss301.kraft.common.enums.Role;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.order_service.repository.OrderRepository;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final OrderRepository userOrderStatisticsRepository;

    public UserAdminService(UserRepository userRepository, OrderRepository userOrderStatisticsRepository) {
        this.userRepository = userRepository;
        this.userOrderStatisticsRepository = userOrderStatisticsRepository;
    }

    public List<UserSummary> listUsers() {
        return userRepository.findAllByRoleAndActiveIsNotNullOrderByCreatedAtDesc(Role.USER)
                .stream().map(this::toSummary).toList();
    }

    public UserSummary getUser(UUID id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (u.getRole() != Role.USER)
            throw new IllegalArgumentException("Not a customer user");
        return toSummary(u);
    }

    public UserSummary toggleActive(UUID id, ToggleActiveRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (u.getRole() != Role.USER)
            throw new IllegalArgumentException("Not a customer user");
        if (req.active() != null)
            u.setActive(req.active());
        u = userRepository.save(u);
        return toSummary(u);
    }

    private UserSummary toSummary(User u) {
        long orderCount = userOrderStatisticsRepository.countByUser(u);
        java.math.BigDecimal totalSpent = userOrderStatisticsRepository.sumPaidTotalByUser(u);
        return new UserSummary(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.isActive(), u.getCreatedAt(),
                orderCount, totalSpent);
    }
}
