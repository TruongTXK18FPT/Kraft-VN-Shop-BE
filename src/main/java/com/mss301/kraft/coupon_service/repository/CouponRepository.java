package com.mss301.kraft.coupon_service.repository;

import com.mss301.kraft.coupon_service.entity.Coupon;
import com.mss301.kraft.coupon_service.enums.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByStatus(CouponStatus status);
    
    @Query("SELECT c FROM Coupon c WHERE c.status = :status AND " +
           "(c.expiresAt IS NULL OR c.expiresAt > :now)")
    List<Coupon> findActiveNotExpired(@Param("status") CouponStatus status, 
                                      @Param("now") OffsetDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.status = 'ACTIVE' AND " +
           "(c.startsAt IS NULL OR c.startsAt <= :now) AND " +
           "(c.expiresAt IS NULL OR c.expiresAt > :now)")
    Optional<Coupon> findActiveByCode(@Param("code") String code, 
                                      @Param("now") OffsetDateTime now);
    
    boolean existsByCode(String code);
}
