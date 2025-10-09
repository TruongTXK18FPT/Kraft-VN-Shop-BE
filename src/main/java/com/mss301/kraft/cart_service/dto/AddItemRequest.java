package com.mss301.kraft.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    private UUID variantId;
    private Integer qty;
}

