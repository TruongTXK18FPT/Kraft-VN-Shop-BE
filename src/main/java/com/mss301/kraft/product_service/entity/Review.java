package com.mss301.kraft.product_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.user_service.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_reviews_product_id", columnList = "product_id"),
        @Index(name = "idx_reviews_user_id", columnList = "user_id")
})
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content")
    private String content;

    @Column(name = "approved", nullable = false)
    private boolean approved = false;
}
