package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.ProductRequest;
import com.mss301.kraft.admin_service.dto.ProductResponse;
import com.mss301.kraft.product_service.entity.Category;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductAdminService {

    private final ProductRepository productRepository;

    public ProductAdminService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(UUID id) {
        return productRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        p.setName(req.getName());
        p.setSku(req.getSku());
        p.setDescription(req.getDescription());
        p.setBrand(req.getBrand());
        p.setCollection(req.getCollection());
        p.setAttributesJson(req.getAttributesJson());
        if (req.getFeatured() != null)
            p.setFeatured(req.getFeatured());
        if (req.getImageUrl() != null)
            p.setImageUrl(req.getImageUrl());
        if (req.getCategoryId() != null) {
            Category c = new Category();
            c.setId(req.getCategoryId());
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
        if (req.getCollection() != null)
            p.setCollection(req.getCollection());
        if (req.getAttributesJson() != null)
            p.setAttributesJson(req.getAttributesJson());
        if (req.getFeatured() != null)
            p.setFeatured(req.getFeatured());
        if (req.getImageUrl() != null)
            p.setImageUrl(req.getImageUrl());
        if (req.getCategoryId() != null) {
            Category c = new Category();
            c.setId(req.getCategoryId());
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
                if (cover == null && v.getMediaJson() != null)
                    cover = v.getMediaJson();
            }
        }
        return new ProductResponse(
                p.getId(), p.getName(), p.getSlug(), p.getSku(), p.getDescription(), p.getBrand(),
                p.getCollection(), p.getAttributesJson(), p.isFeatured(), p.getImageUrl(), p.getStatus(),
                p.getCategory() != null ? p.getCategory().getId() : null, priceMin, stockTotal, cover,
                null); // variants list - null for basic response
    }
}
