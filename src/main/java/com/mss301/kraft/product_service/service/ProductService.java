package com.mss301.kraft.product_service.service;

import com.mss301.kraft.product_service.dto.ProductRequest;
import com.mss301.kraft.product_service.dto.ProductResponse;
import com.mss301.kraft.product_service.dto.ProductPageResponse;
import com.mss301.kraft.product_service.dto.FacetItem;
import com.mss301.kraft.product_service.entity.Category;
import com.mss301.kraft.product_service.entity.Collection;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.enums.ProductStatus;
import com.mss301.kraft.product_service.repository.CategoryRepository;
import com.mss301.kraft.product_service.repository.CollectionRepository;
import com.mss301.kraft.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CollectionRepository collectionRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
            CollectionRepository collectionRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.collectionRepository = collectionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> listActive() {
        return productRepository.findAllByStatus(ProductStatus.ACTIVE).stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> listFeatured() {
        return productRepository.findAllByFeaturedTrueAndStatus(ProductStatus.ACTIVE).stream()
                .map(this::toResponse).toList();
    }

    public List<ProductResponse> listOnSale() {
        return productRepository.findAllOnSale(ProductStatus.ACTIVE).stream().map(this::toResponse).toList();
    }

    public ProductResponse get(UUID id) {
        return productRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public ProductResponse getBySlug(String slug) {
        return productRepository.findBySlug(slug).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<ProductResponse> searchProducts(String query, List<UUID> collectionIds, List<UUID> categoryIds) {
        List<UUID> safeCollectionIds = collectionIds == null ? java.util.List.of() : collectionIds;
        List<UUID> safeCategoryIds = categoryIds == null ? java.util.List.of() : categoryIds;
        boolean collectionIdsEmpty = safeCollectionIds.isEmpty();
        boolean categoryIdsEmpty = safeCategoryIds.isEmpty();
        String q = query == null || query.isBlank() ? null : "%" + query.toLowerCase() + "%";
        return productRepository
                .searchProducts(ProductStatus.ACTIVE, q, safeCollectionIds, safeCategoryIds, collectionIdsEmpty,
                        categoryIdsEmpty)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductPageResponse searchProductsPaged(String query,
            List<UUID> collectionIds, List<UUID> categoryIds, int page, int size) {
        List<UUID> safeCollectionIds = collectionIds == null ? java.util.List.of() : collectionIds;
        List<UUID> safeCategoryIds = categoryIds == null ? java.util.List.of() : categoryIds;
        boolean collectionIdsEmpty = safeCollectionIds.isEmpty();
        boolean categoryIdsEmpty = safeCategoryIds.isEmpty();
        Pageable pageable = PageRequest.of(page, size);
        String q = query == null || query.isBlank() ? null : "%" + query.toLowerCase() + "%";
        Page<Product> result = productRepository
                .searchProductsPaged(ProductStatus.ACTIVE, q, safeCollectionIds, safeCategoryIds,
                        collectionIdsEmpty, categoryIdsEmpty, pageable);
        ProductPageResponse resp = new ProductPageResponse();
        resp.setItems(result.getContent().stream().map(this::toResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    public List<FacetItem> facetCollections(String query, List<UUID> categoryIds) {
        List<UUID> safeCategoryIds = categoryIds == null ? java.util.List.of() : categoryIds;
        boolean categoryIdsEmpty = safeCategoryIds.isEmpty();
        String q = query == null || query.isBlank() ? null : "%" + query.toLowerCase() + "%";
        List<Object[]> rows = productRepository
                .facetCountsByCollection(ProductStatus.ACTIVE, q, safeCategoryIds, categoryIdsEmpty);
        List<FacetItem> items = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            java.util.UUID id = (java.util.UUID) row[0];
            String name = (String) row[1];
            Long cnt = (Long) row[2];
            items.add(new FacetItem(id, name, cnt));
        }
        return items;
    }

    public List<FacetItem> facetCategories(String query, List<UUID> collectionIds) {
        List<UUID> safeCollectionIds = collectionIds == null ? java.util.List.of() : collectionIds;
        boolean collectionIdsEmpty = safeCollectionIds.isEmpty();
        String q = query == null || query.isBlank() ? null : "%" + query.toLowerCase() + "%";
        List<Object[]> rows = productRepository
                .facetCountsByCategory(ProductStatus.ACTIVE, q, safeCollectionIds, collectionIdsEmpty);
        List<FacetItem> items = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            java.util.UUID id = (java.util.UUID) row[0];
            String name = (String) row[1];
            Long cnt = (Long) row[2];
            items.add(new FacetItem(id, name, cnt));
        }
        return items;
    }

    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        p.setName(req.getName());
        p.setSku(req.getSku());
        p.setDescription(req.getDescription());
        p.setBrand(req.getBrand());

        if (req.getCollectionId() != null) {
            Collection collection = collectionRepository.findById(req.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
            p.setCollection(collection);
        }

        p.setAttributesJson(req.getAttributesJson());
        if (req.getFeatured() != null)
            p.setFeatured(req.getFeatured());
        if (req.getImageUrl() != null)
            p.setImageUrl(req.getImageUrl());
        if (req.getStatus() != null)
            p.setStatus(req.getStatus());
        if (req.getCategoryId() != null) {
            Category c = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            p.setCategory(c);
        }
        p = productRepository.save(p);
        return toResponse(p);
    }

    public ProductResponse update(UUID id, ProductRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (req.getName() != null)
            p.setName(req.getName());
        if (req.getSku() != null)
            p.setSku(req.getSku());
        if (req.getDescription() != null)
            p.setDescription(req.getDescription());
        if (req.getBrand() != null)
            p.setBrand(req.getBrand());

        if (req.getCollectionId() != null) {
            Collection collection = collectionRepository.findById(req.getCollectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
            p.setCollection(collection);
        }

        if (req.getAttributesJson() != null)
            p.setAttributesJson(req.getAttributesJson());
        if (req.getFeatured() != null)
            p.setFeatured(req.getFeatured());
        if (req.getImageUrl() != null)
            p.setImageUrl(req.getImageUrl());
        if (req.getStatus() != null)
            p.setStatus(req.getStatus());
        if (req.getCategoryId() != null) {
            Category c = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            p.setCategory(c);
        }
        p = productRepository.save(p);
        return toResponse(p);
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        java.math.BigDecimal priceMin = null;
        java.math.BigDecimal priceMinOriginal = null;
        java.math.BigDecimal priceMinSale = null;
        int stockTotal = 0;
        String cover = null;
        if (p.getVariants() != null && !p.getVariants().isEmpty()) {
            for (var v : p.getVariants()) {
                java.math.BigDecimal base = v.getPrice();
                java.math.BigDecimal sale = v.getSalePrice();
                if (base != null) {
                    priceMinOriginal = priceMinOriginal == null || base.compareTo(priceMinOriginal) < 0 ? base
                            : priceMinOriginal;
                }
                if (sale != null) {
                    priceMinSale = priceMinSale == null || sale.compareTo(priceMinSale) < 0 ? sale : priceMinSale;
                }
                java.math.BigDecimal candidate = sale != null ? sale : base;
                if (candidate != null) {
                    priceMin = priceMin == null || candidate.compareTo(priceMin) < 0 ? candidate : priceMin;
                }
                if (v.getStock() != null)
                    stockTotal += v.getStock();
                if (cover == null && v.getImageUrl() != null)
                    cover = v.getImageUrl();
            }
        }

        ProductResponse response = new ProductResponse();
        response.setId(p.getId());
        response.setName(p.getName());
        response.setSlug(p.getSlug());
        response.setSku(p.getSku());
        response.setDescription(p.getDescription());
        response.setBrand(p.getBrand());
        response.setCollectionId(p.getCollection() != null ? p.getCollection().getId() : null);
        response.setCollectionName(p.getCollection() != null ? p.getCollection().getName() : null);
        response.setAttributesJson(p.getAttributesJson());
        response.setFeatured(p.isFeatured());
        response.setImageUrl(p.getImageUrl());
        response.setStatus(p.getStatus());
        response.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
        response.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
        response.setPriceMin(priceMin);
        response.setPriceMinOriginal(priceMinOriginal);
        response.setPriceMinSale(priceMinSale);
        response.setStockTotal(stockTotal);
        response.setCoverImage(cover);

        return response;
    }
}
