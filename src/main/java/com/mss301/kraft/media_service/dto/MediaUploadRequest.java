package com.mss301.kraft.media_service.dto;

import com.mss301.kraft.media_service.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadRequest {
    
    private MediaType mediaType;
    
    private UUID entityId;
    
    private String entityType;
    
    private String altText;
    
    private String caption;
    
    private String folder; // Optional custom folder
}
