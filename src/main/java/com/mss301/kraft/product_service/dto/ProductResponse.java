package com.mss301.kraft.product_service.dto;

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
    private UUID collectionId;
    private String collectionName;
    private String attributesJson;
    private boolean featured;
    private String imageUrl; // Main product image URL
    private ProductStatus status;
    private UUID categoryId;
    private String categoryName;

    // Derived from variants
    private BigDecimal priceMin; // effective min for display (sale if any)
    private BigDecimal priceMinOriginal; // min base price (without sale)
    private BigDecimal priceMinSale; // min sale price if any
    private Integer stockTotal;
    private String coverImage; // First image from variants

    // Variants list (optional, for detailed views)
    private List<ProductVariantResponse> variants;
}
