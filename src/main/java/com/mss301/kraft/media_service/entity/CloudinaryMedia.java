package com.mss301.kraft.media_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.media_service.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "cloudinary_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudinaryMedia extends BaseEntity {

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    private Integer width;

    private Integer height;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(nullable = false)
    private String folder;

    private String altText;

    private String caption;

    // Reference to entity (product, user, etc.)
    private UUID entityId;

    private String entityType; // PRODUCT, USER, CATEGORY, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
