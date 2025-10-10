package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ProductResponse;
import com.mss301.kraft.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public list of ACTIVE products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listActive() {
        return ResponseEntity.ok(productService.listActive());
    }

    // Featured (Hot) products
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> listFeatured() {
        return ResponseEntity.ok(productService.listFeatured());
    }

    // Products that have any variant with sale price
    @GetMapping("/sale")
    public ResponseEntity<List<ProductResponse>> listOnSale() {
        return ResponseEntity.ok(productService.listOnSale());
    }

    // Public product detail by slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    // Search products with filters
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String collectionId,
            @RequestParam(required = false) String categoryId) {
        
        UUID collectionUuid = collectionId != null ? UUID.fromString(collectionId) : null;
        UUID categoryUuid = categoryId != null ? UUID.fromString(categoryId) : null;
        
        return ResponseEntity.ok(productService.searchProducts(query, collectionUuid, categoryUuid));
    }
}
