package com.mss301.kraft.product_service.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private UUID id;
    private UUID productId;
    private String productName;
    private UUID userId;
    private String userName;
    private Integer rating;
    private String content;
    private boolean approved;
    private boolean purchaseVerified;
    private String adminResponse;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
