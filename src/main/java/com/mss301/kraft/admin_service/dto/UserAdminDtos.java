package com.mss301.kraft.admin_service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class UserAdminDtos {
    public record UserSummary(
            UUID id,
            String name,
            String email,
            String phone,
            boolean active,
            OffsetDateTime createdAt,
            long totalOrders,
            BigDecimal totalSpent) {
    }

    public record ToggleActiveRequest(Boolean active) {
    }
}
