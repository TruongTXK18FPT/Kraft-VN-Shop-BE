package com.mss301.kraft.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private UUID id;
    private UUID variantId;
    private String sku;
    private String color;
    private String size;
    private String imageUrl;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}

