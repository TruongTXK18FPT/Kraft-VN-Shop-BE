package com.mss301.kraft.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacetItem {
    private UUID id;
    private String name;
    private Long count;
}
