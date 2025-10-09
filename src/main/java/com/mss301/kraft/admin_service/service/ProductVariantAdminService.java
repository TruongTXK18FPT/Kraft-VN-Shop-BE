package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.ProductVariantRequest;
import com.mss301.kraft.admin_service.dto.ProductVariantResponse;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.entity.ProductVariant;
import com.mss301.kraft.product_service.repository.ProductRepository;
import com.mss301.kraft.product_service.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductVariantAdminService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantAdminService(ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public List<ProductVariantResponse> list(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return productVariantRepository.findByProduct(product).stream().map(this::toResponse).toList();
    }

    public ProductVariantResponse create(UUID productId, ProductVariantRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductVariant v = new ProductVariant();
        v.setProduct(product);
        apply(v, req);
        validateNumbers(v);
        validatePricing(v);
        ProductVariant saved = productVariantRepository.save(v);
        return toResponse(saved);
    }

    public ProductVariantResponse update(UUID variantId, ProductVariantRequest req) {
        ProductVariant v = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));
        apply(v, req);
        validateNumbers(v);
        validatePricing(v);
        ProductVariant saved = productVariantRepository.save(v);
        return toResponse(saved);
    }

    public void delete(UUID variantId) {
        productVariantRepository.deleteById(variantId);
    }

    private void apply(ProductVariant v, ProductVariantRequest req) {
        if (req.getColor() != null)
            v.setColor(req.getColor());
        if (req.getSize() != null)
            v.setSize(req.getSize());
        if (req.getSku() != null)
            v.setSku(req.getSku());
        if (req.getPrice() != null)
            v.setPrice(req.getPrice());
        if (req.getSalePrice() != null)
            v.setSalePrice(req.getSalePrice());
        if (req.getStock() != null)
            v.setStock(req.getStock());
        if (req.getImageUrl() != null)
            v.setImageUrl(req.getImageUrl());
    }

    private ProductVariantResponse toResponse(ProductVariant v) {
        return new ProductVariantResponse(v.getId(), v.getColor(), v.getSize(), v.getSku(), v.getPrice(),
                v.getSalePrice(), v.getStock(), v.getImageUrl());
    }

    private void validatePricing(ProductVariant v) {
        if (v.getSalePrice() != null) {
            if (v.getPrice() == null) {
                throw new IllegalArgumentException("Price is required when sale price is set");
            }
            if (v.getSalePrice().compareTo(v.getPrice()) >= 0) {
                throw new IllegalArgumentException("Sale price must be lower than price");
            }
        }
    }

    private void validateNumbers(ProductVariant v) {
        if (v.getPrice() != null && v.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (v.getSalePrice() != null && v.getSalePrice().signum() < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative");
        }
        if (v.getStock() != null && v.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }
}
