package com.mss301.kraft.order_service.repository;

import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Find all orders by user with eager loading of user, items, productVariant,
    // and product
    @EntityGraph(attributePaths = { "user", "items", "items.productVariant", "items.productVariant.product" })
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") UUID userId);

    // Find order by code with eager loading
    @EntityGraph(attributePaths = { "user", "items", "items.productVariant", "items.productVariant.product" })
    Optional<Order> findByCode(String code);

    // Find all orders with eager loading for admin
    @EntityGraph(attributePaths = { "user", "items", "items.productVariant", "items.productVariant.product" })
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrders();

    // User statistics methods (moved from user_service)
    long countByUser(User user);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.user = :user and o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID")
    BigDecimal sumPaidTotalByUser(@Param("user") User user);
    
    // Dashboard analytics methods
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID")
    BigDecimal sumTotalRevenue();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt < :date")
    Long countByCreatedAtBefore(@Param("date") OffsetDateTime date);
    
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID AND o.createdAt < :date")
    BigDecimal sumRevenueByCreatedAtBefore(@Param("date") OffsetDateTime date);
    
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countByCreatedAtBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
