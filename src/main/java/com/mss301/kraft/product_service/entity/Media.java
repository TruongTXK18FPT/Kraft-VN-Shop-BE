package com.mss301.kraft.product_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.product_service.enums.MediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_media_product_id", columnList = "product_id")
})
public class Media extends BaseEntity {

    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MediaType type = MediaType.IMAGE;

    @Column(name = "alt")
    private String alt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "is_360")
    private boolean is360 = false;

    @Column(name = "is_video")
    private boolean isVideo = false;

    @Column(name = "is_model3d")
    private boolean isModel3d = false;
}
