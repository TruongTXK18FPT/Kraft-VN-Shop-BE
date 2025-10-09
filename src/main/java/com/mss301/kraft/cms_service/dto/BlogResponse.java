package com.mss301.kraft.cms_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponse {
    private UUID id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String category;
    private String imageUrl;
    private String author;
    private String readTime;
    private Boolean featured;
    private Boolean published;
    private String tags;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
