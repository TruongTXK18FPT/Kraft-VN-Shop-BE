package com.mss301.kraft.admin_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BulkSaleUpdateRequest {
    private List<UUID> variantIds;
    private Boolean onSale;
    private BigDecimal salePrice;
}
