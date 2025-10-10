package com.mss301.kraft.product_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class FacetResponse {
    private List<FacetItem> collections;
    private List<FacetItem> categories;
}
