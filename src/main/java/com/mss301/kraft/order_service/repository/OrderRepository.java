package com.mss301.kraft.order_service.repository;

import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Find all orders by user
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") UUID userId);

    // Find order by code
    Optional<Order> findByCode(String code);

    // Find all orders
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrders();

    // User statistics methods (moved from user_service)
    long countByUser(User user);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.user = :user and o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID")
    BigDecimal sumPaidTotalByUser(@Param("user") User user);
}
