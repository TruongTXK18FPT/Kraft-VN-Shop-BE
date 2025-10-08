package com.mss301.kraft.media_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUpdateRequest {
    
    private String altText;
    
    private String caption;
    
    private UUID entityId;
    
    private String entityType;
}
