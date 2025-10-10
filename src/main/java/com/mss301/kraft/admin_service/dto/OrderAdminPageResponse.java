package com.mss301.kraft.admin_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderAdminPageResponse {
    private List<OrderAdminResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
