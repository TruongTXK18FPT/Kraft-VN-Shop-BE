package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}

