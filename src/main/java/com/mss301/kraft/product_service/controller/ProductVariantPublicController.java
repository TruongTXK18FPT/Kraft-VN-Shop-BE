package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ProductVariantResponse;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.repository.ProductRepository;
import com.mss301.kraft.product_service.service.ProductVariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/slug/{slug}/variants")
public class ProductVariantPublicController {

    private final ProductRepository productRepository;
    private final ProductVariantService productVariantService;

    public ProductVariantPublicController(ProductRepository productRepository,
            ProductVariantService productVariantService) {
        this.productRepository = productRepository;
        this.productVariantService = productVariantService;
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> list(@PathVariable String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        // Reuse variant service listing by productId
        return ResponseEntity.ok(productVariantService.list(product.getId()));
    }
}
