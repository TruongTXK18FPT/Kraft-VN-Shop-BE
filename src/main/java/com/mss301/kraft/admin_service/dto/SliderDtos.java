package com.mss301.kraft.admin_service.dto;

import java.util.UUID;

public class SliderDtos {
    public record SliderRequest(
            String title,
            String subtitle,
            String description,
            String imageUrl,
            String ctaLabel,
            String ctaLink,
            Integer position,
            Boolean active) {
    }

    public record SliderResponse(
            UUID id,
            String title,
            String subtitle,
            String description,
            String imageUrl,
            String ctaLabel,
            String ctaLink,
            Integer position,
            boolean active) {
    }
}

