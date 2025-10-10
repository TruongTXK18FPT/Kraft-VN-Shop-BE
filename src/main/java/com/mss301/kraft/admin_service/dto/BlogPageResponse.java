package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.cms_service.dto.BlogResponse;
import lombok.Data;
import java.util.List;

@Data
public class BlogPageResponse {
    private List<BlogResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
