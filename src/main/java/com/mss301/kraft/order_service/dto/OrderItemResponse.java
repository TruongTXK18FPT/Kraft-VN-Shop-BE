package com.mss301.kraft.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private UUID variantId;
    private String productName;
    private String sku;
    private String color;
    private String size;
    private String imageUrl;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal lineTotal;
}
