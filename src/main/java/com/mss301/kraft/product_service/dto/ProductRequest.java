package com.mss301.kraft.product_service.dto;

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
    private String collection;
    private String attributesJson;
    private Boolean featured;
    private UUID categoryId;
}
