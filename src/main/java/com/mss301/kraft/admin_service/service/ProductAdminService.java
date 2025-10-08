package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.ProductRequest;
import com.mss301.kraft.admin_service.dto.ProductResponse;
import com.mss301.kraft.product_service.entity.Category;
import com.mss301.kraft.product_service.entity.Collection;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.repository.CategoryRepository;
import com.mss301.kraft.product_service.repository.CollectionRepository;
import com.mss301.kraft.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final CollectionRepository collectionRepository;
    private final CategoryRepository categoryRepository;

    public ProductAdminService(ProductRepository productRepository,
            CollectionRepository collectionRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.collectionRepository = collectionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(UUID id) {
        return productRepository.findByIdWithRelations(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        p.setName(req.getName());
        p.setSku(req.getSku());
        p.setDescription(req.getDescription());
        p.setBrand(req.getBrand());

        // Handle Collection entity
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

    @Transactional
    public ProductResponse update(UUID id, ProductRequest req) {
        Product p = productRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (req.getName() != null)
            p.setName(req.getName());
        if (req.getSku() != null)
            p.setSku(req.getSku());
        if (req.getDescription() != null)
            p.setDescription(req.getDescription());
        if (req.getBrand() != null)
            p.setBrand(req.getBrand());

        // Handle Collection entity
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
        int stockTotal = 0;
        String cover = null;
        if (p.getVariants() != null && !p.getVariants().isEmpty()) {
            for (var v : p.getVariants()) {
                if (v.getPrice() != null) {
                    priceMin = priceMin == null || v.getPrice().compareTo(priceMin) < 0 ? v.getPrice() : priceMin;
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
        response.setStockTotal(stockTotal);
        response.setCoverImage(cover);

        return response;
    }
}
