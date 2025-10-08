package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.product_service.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String sku;
    private String description;
    private String brand;
    private UUID collectionId;
    private String attributesJson;
    private Boolean featured;
    private String imageUrl; // URL to the uploaded image
    private UUID categoryId;
    private ProductStatus status;
}
