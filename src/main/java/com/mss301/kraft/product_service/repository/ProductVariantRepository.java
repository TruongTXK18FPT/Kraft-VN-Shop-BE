package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProduct(Product product);
}

