package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ProductResponse;
import com.mss301.kraft.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@PreAuthorize("permitAll()")
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
}
