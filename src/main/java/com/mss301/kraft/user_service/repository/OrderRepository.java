package com.mss301.kraft.user_service.repository;

import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    long countByUser(User user);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.user = :user and o.paymentStatus = com.mss301.kraft.common.enums.PaymentStatus.PAID")
    BigDecimal sumPaidTotalByUser(@Param("user") User user);
}
