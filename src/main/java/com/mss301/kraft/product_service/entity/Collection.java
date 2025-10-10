package com.mss301.kraft.product_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.common.SlugUtils;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "collections", indexes = {
        @Index(name = "idx_collections_slug", columnList = "slug", unique = true)
})
public class Collection extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void generateSlug() {
        if (this.slug == null || this.slug.isBlank()) {
            this.slug = SlugUtils.toSlug(this.name);
        }
    }
}
