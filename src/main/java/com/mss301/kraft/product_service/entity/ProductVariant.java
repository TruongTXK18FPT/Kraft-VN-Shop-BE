package com.mss301.kraft.product_service.entity;

import com.mss301.kraft.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product_variants", indexes = {
                @Index(name = "idx_product_variants_sku", columnList = "sku", unique = true),
                @Index(name = "idx_product_variants_product_id", columnList = "product_id")
}, uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_variant_color_size", columnNames = { "product_id", "color",
                                "size" })
})
public class ProductVariant extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", nullable = false)
        private Product product;

        @Column(name = "color")
        private String color;

        @Column(name = "size")
        private String size;

        @Column(name = "sku", unique = true)
        private String sku;

        @Column(name = "price", nullable = false)
        private BigDecimal price;

        @Column(name = "sale_price")
        private BigDecimal salePrice;

        @Column(name = "on_sale")
        private Boolean onSale = false;

        @Column(name = "stock")
        private Integer stock = 0;

        @Column(name = "image_url")
        private String imageUrl; // Direct URL to variant image
}
