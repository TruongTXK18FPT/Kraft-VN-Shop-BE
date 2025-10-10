package com.mss301.kraft.admin_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductPageResponse {
    private List<ProductResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
