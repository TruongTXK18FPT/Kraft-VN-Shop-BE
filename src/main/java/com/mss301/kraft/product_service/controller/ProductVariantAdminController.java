package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ProductVariantRequest;
import com.mss301.kraft.product_service.dto.ProductVariantResponse;
import com.mss301.kraft.product_service.service.ProductVariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products/{productId}/variants")
@PreAuthorize("hasRole('ADMIN')")
public class ProductVariantAdminController {

    private final ProductVariantService variantService;

    public ProductVariantAdminController(ProductVariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> list(@PathVariable UUID productId) {
        return ResponseEntity.ok(variantService.list(productId));
    }

    @PostMapping
    public ResponseEntity<ProductVariantResponse> create(@PathVariable UUID productId,
            @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantService.create(productId, request));
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponse> update(@PathVariable UUID productId, @PathVariable UUID variantId,
            @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantService.update(variantId, request));
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId, @PathVariable UUID variantId) {
        variantService.delete(variantId);
        return ResponseEntity.noContent().build();
    }
}

