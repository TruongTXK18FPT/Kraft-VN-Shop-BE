package com.mss301.kraft.cms_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.common.SlugUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String author;

    @Column(name = "read_time")
    private String readTime; // e.g. "5 phút đọc"

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "is_published")
    @Builder.Default
    private Boolean published = false;

    private String tags; // Comma-separated tags

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = SlugUtils.toSlug(this.title);
        }
    }
}
