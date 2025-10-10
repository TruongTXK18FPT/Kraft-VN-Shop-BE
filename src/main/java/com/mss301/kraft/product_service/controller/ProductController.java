package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ProductResponse;
import com.mss301.kraft.product_service.dto.FacetResponse;
import com.mss301.kraft.product_service.dto.ProductPageResponse;
import com.mss301.kraft.product_service.service.ProductService;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false, name = "collectionId") List<String> collectionIds,
            @RequestParam(required = false, name = "categoryId") List<String> categoryIds) {

        List<UUID> collectionUuids = collectionIds == null
                ? java.util.List.of()
                : collectionIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();
        List<UUID> categoryUuids = categoryIds == null
                ? java.util.List.of()
                : categoryIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();

        return ResponseEntity.ok(productService.searchProducts(query, collectionUuids, categoryUuids));
    }

    // Paged search (20 per page default)
    @GetMapping("/search-paged")
    public ResponseEntity<ProductPageResponse> searchProductsPaged(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, name = "collectionId") List<String> collectionIds,
            @RequestParam(required = false, name = "categoryId") List<String> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        List<UUID> collectionUuids = collectionIds == null
                ? java.util.List.of()
                : collectionIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();
        List<UUID> categoryUuids = categoryIds == null
                ? java.util.List.of()
                : categoryIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();
        return ResponseEntity.ok(productService.searchProductsPaged(query, collectionUuids, categoryUuids, page, size));
    }

    @GetMapping("/facets")
    public ResponseEntity<FacetResponse> facetCounts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, name = "collectionId") List<String> collectionIds,
            @RequestParam(required = false, name = "categoryId") List<String> categoryIds) {

        List<UUID> collectionUuids = collectionIds == null
                ? java.util.List.of()
                : collectionIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();
        List<UUID> categoryUuids = categoryIds == null
                ? java.util.List.of()
                : categoryIds.stream().filter(java.util.Objects::nonNull).map(UUID::fromString).toList();

        FacetResponse response = new FacetResponse();
        response.setCollections(productService.facetCollections(query, categoryUuids));
        response.setCategories(productService.facetCategories(query, collectionUuids));
        return ResponseEntity.ok(response);
    }
}
