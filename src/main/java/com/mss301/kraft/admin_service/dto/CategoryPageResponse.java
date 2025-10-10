package com.mss301.kraft.admin_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryPageResponse {
    private List<CategoryResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
