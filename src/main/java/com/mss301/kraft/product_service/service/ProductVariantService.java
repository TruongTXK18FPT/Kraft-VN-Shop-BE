package com.mss301.kraft.product_service.service;

import com.mss301.kraft.product_service.dto.ProductVariantRequest;
import com.mss301.kraft.product_service.dto.ProductVariantResponse;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.entity.ProductVariant;
import com.mss301.kraft.product_service.repository.ProductRepository;
import com.mss301.kraft.product_service.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantService(ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    public List<ProductVariantResponse> list(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return productVariantRepository.findByProduct(product).stream().map(this::toResponse).toList();
    }

    private ProductVariantResponse toResponse(ProductVariant v) {
        return new ProductVariantResponse(v.getId(), v.getColor(), v.getSize(), v.getSku(), v.getPrice(),
                v.getSalePrice(), v.getStock(), v.getMediaJson());
    }
}
