package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.ProductVariantRequest;
import com.mss301.kraft.admin_service.dto.ProductVariantResponse;
import com.mss301.kraft.admin_service.service.ProductVariantAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products/{productId}/variants")
@PreAuthorize("hasRole('ADMIN')")
public class ProductVariantAdminController {

    private final ProductVariantAdminService variantAdminService;

    public ProductVariantAdminController(ProductVariantAdminService variantAdminService) {
        this.variantAdminService = variantAdminService;
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> list(@PathVariable UUID productId) {
        return ResponseEntity.ok(variantAdminService.list(productId));
    }

    @PostMapping
    public ResponseEntity<ProductVariantResponse> create(@PathVariable UUID productId,
            @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantAdminService.create(productId, request));
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponse> update(@PathVariable UUID productId, @PathVariable UUID variantId,
            @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantAdminService.update(variantId, request));
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId, @PathVariable UUID variantId) {
        variantAdminService.delete(variantId);
        return ResponseEntity.noContent().build();
    }
}
