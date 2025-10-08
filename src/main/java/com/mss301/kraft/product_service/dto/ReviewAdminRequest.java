package com.mss301.kraft.product_service.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAdminRequest {

    private boolean approved;

    @Size(max = 500, message = "Admin response cannot exceed 500 characters")
    private String adminResponse;
}
