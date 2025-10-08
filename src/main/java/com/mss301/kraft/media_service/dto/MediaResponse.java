package com.mss301.kraft.media_service.dto;

import com.mss301.kraft.media_service.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    
    private UUID id;
    
    private String url;
    
    private String publicId;
    
    private String originalFileName;
    
    private Long fileSize;
    
    private String contentType;
    
    private Integer width;
    
    private Integer height;
    
    private MediaType mediaType;
    
    private String folder;
    
    private String altText;
    
    private String caption;
    
    private UUID entityId;
    
    private String entityType;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
