package com.mss301.kraft.admin_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequest {
    private String name;
    private String description;
    private String imageUrl;
}
