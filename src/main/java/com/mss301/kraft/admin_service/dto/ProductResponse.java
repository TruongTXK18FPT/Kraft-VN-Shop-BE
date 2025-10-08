package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.product_service.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private String brand;
    private String collection;
    private String attributesJson;
    private boolean featured;
    private String imageUrl; // Main product image URL
    private ProductStatus status;
    private UUID categoryId;

    // Derived from variants
    private BigDecimal priceMin;
    private Integer stockTotal;
    private String coverImage; // First image from variants

    // Variants list (optional, for detailed views)
    private List<ProductVariantResponse> variants;
}
