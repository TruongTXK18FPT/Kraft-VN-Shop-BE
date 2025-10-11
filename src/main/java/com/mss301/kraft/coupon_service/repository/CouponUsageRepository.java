package com.mss301.kraft.coupon_service.repository;

import com.mss301.kraft.coupon_service.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {

    List<CouponUsage> findByCouponId(UUID couponId);

    List<CouponUsage> findByUserId(UUID userId);

    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.user.id = :userId")
    Long countByCouponIdAndUserId(@Param("couponId") UUID couponId,
            @Param("userId") UUID userId);

    boolean existsByOrderId(UUID orderId);

    @Query("SELECT cu FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.user.id = :userId ORDER BY cu.createdAt DESC")
    List<CouponUsage> findByCouponIdAndUserIdOrderByCreatedAtDesc(@Param("couponId") UUID couponId,
            @Param("userId") UUID userId);
}
