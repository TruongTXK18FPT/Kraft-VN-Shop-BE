package com.mss301.kraft.admin_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CollectionPageResponse {
    private List<CollectionResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
