package com.mss301.kraft.admin_service.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Integer productCount;
}
