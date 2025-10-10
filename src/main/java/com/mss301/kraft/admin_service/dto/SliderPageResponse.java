package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.admin_service.dto.SliderDtos.SliderResponse;
import lombok.Data;
import java.util.List;

@Data
public class SliderPageResponse {
    private List<SliderResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
