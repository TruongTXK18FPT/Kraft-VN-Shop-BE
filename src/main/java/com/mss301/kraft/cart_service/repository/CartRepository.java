package com.mss301.kraft.cart_service.repository;

import com.mss301.kraft.cart_service.entity.Cart;
import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    @EntityGraph(attributePaths = { "items", "items.productVariant", "items.productVariant.product" })
    Optional<Cart> findById(UUID id);

    @EntityGraph(attributePaths = { "items", "items.productVariant", "items.productVariant.product" })
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.createdAt DESC")
    List<Cart> findAllByUser(@Param("user") User user);

    /**
     * Find the most recent cart for a user
     * If multiple carts exist (due to duplicates), returns the newest one
     * Note: Returns all carts ordered by creation date, caller should get first()
     */
    @EntityGraph(attributePaths = { "items", "items.productVariant", "items.productVariant.product" })
    @Query("SELECT c FROM Cart c WHERE c.user = :user ORDER BY c.createdAt DESC")
    List<Cart> findByUser(@Param("user") User user);
}
