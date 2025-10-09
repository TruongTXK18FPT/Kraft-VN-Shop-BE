package com.mss301.kraft.cart_service.repository;

import com.mss301.kraft.cart_service.entity.Cart;
import com.mss301.kraft.cart_service.entity.CartItem;
import com.mss301.kraft.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant productVariant);

    List<CartItem> findByCart(Cart cart);
}

