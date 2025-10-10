package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.admin_service.dto.UserAdminDtos.UserSummary;
import lombok.Data;
import java.util.List;

@Data
public class UserAdminPageResponse {
    private List<UserSummary> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
