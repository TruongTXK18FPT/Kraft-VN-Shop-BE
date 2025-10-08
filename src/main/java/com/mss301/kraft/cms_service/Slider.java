package com.mss301.kraft.cms_service;

import com.mss301.kraft.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slider", indexes = {
        @Index(name = "idx_slider_active", columnList = "active")
})
public class Slider extends BaseEntity {

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    // Temporary image URL. Later will be switched to cloud asset ID
    @Column(name = "image_url", length = 1024)
    private String imageUrl;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "cta_label", length = 100)
    private String ctaLabel;

    @Column(name = "cta_link", length = 1024)
    private String ctaLink;

    @Column(name = "position")
    private Integer position; // lower -> earlier

    @Column(name = "active")
    private boolean active = true;
}
