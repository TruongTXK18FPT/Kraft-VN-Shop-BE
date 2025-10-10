package com.mss301.kraft.coupon_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss301.kraft.coupon_service.dto.*;
import com.mss301.kraft.coupon_service.entity.Coupon;
import com.mss301.kraft.coupon_service.entity.CouponUsage;
import com.mss301.kraft.coupon_service.enums.CouponStatus;
import com.mss301.kraft.coupon_service.enums.CouponType;
import com.mss301.kraft.coupon_service.exception.CouponAlreadyExistsException;
import com.mss301.kraft.coupon_service.exception.CouponNotFoundException;
import com.mss301.kraft.coupon_service.repository.CouponRepository;
import com.mss301.kraft.coupon_service.repository.CouponUsageRepository;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new CouponAlreadyExistsException("Mã coupon đã tồn tại: " + request.getCode());
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .value(request.getValue())
                .conditionsJson(convertConditionsToJson(request.getConditions()))
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .status(request.getStatus() != null ? request.getStatus() : CouponStatus.ACTIVE)
                .startsAt(request.getStartsAt())
                .expiresAt(request.getExpiresAt())
                .showInBanner(request.getShowInBanner() != null ? request.getShowInBanner() : false)
                .build();

        coupon = couponRepository.save(coupon);
        log.info("Created new coupon: {}", coupon.getCode());

        return convertToResponse(coupon);
    }

    public CouponResponse getCouponById(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
        return convertToResponse(coupon);
    }

    public Coupon getCouponEntityById(UUID id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }

    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponNotFoundException(code, true));
        return convertToResponse(coupon);
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public com.mss301.kraft.admin_service.dto.CouponPageResponse getAllCouponsPaged(int page, int size) {
        Page<Coupon> result = couponRepository.findAll(PageRequest.of(page, size));
        com.mss301.kraft.admin_service.dto.CouponPageResponse resp = new com.mss301.kraft.admin_service.dto.CouponPageResponse();
        resp.setItems(result.getContent().stream().map(this::convertToResponse).toList());
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findActiveNotExpired(CouponStatus.ACTIVE, OffsetDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CouponResponse> getBannerCoupons() {
        return couponRepository.findActiveNotExpired(CouponStatus.ACTIVE, OffsetDateTime.now()).stream()
                .filter(coupon -> Boolean.TRUE.equals(coupon.getShowInBanner()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public boolean couponCodeExists(String code) {
        return couponRepository.existsByCode(code);
    }

    @Transactional
    public CouponResponse updateCoupon(UUID id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));

        if (!coupon.getCode().equals(request.getCode()) &&
                couponRepository.existsByCode(request.getCode())) {
            throw new CouponAlreadyExistsException("Mã coupon đã tồn tại: " + request.getCode());
        }

        coupon.setCode(request.getCode());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setType(request.getType());
        coupon.setValue(request.getValue());
        coupon.setConditionsJson(convertConditionsToJson(request.getConditions()));
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsageLimitPerUser(request.getUsageLimitPerUser());
        coupon.setStatus(request.getStatus());
        coupon.setStartsAt(request.getStartsAt());
        coupon.setExpiresAt(request.getExpiresAt());
        coupon.setShowInBanner(request.getShowInBanner());

        coupon = couponRepository.save(coupon);
        log.info("Updated coupon: {}", coupon.getCode());

        return convertToResponse(coupon);
    }

    @Transactional
    public void deleteCoupon(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));

        couponRepository.delete(coupon);
        log.info("Deleted coupon: {}", coupon.getCode());
    }

    public CouponValidationResponse validateCoupon(ValidateCouponRequest request) {
        log.info("Validating coupon: {} for user: {} with order total: {}", 
                request.getCouponCode(), request.getUserId(), request.getOrderTotal());
        
        Coupon coupon = couponRepository.findActiveByCode(request.getCouponCode(), OffsetDateTime.now())
                .orElse(null);

        if (coupon == null) {
            log.warn("Coupon not found or not active: {}", request.getCouponCode());
            return CouponValidationResponse.builder()
                    .valid(false)
                    .message("Mã giảm giá không hợp lệ hoặc đã hết hạn")
                    .build();
        }
        
        log.info("Found coupon: {} - Status: {}, Starts: {}, Expires: {}", 
                coupon.getCode(), coupon.getStatus(), coupon.getStartsAt(), coupon.getExpiresAt());

        // Check usage limit per user
        if (coupon.getUsageLimitPerUser() != null) {
            Long userUsageCount = couponUsageRepository.countByCouponIdAndUserId(
                    coupon.getId(), request.getUserId());
            
            log.info("User usage count: {} / {}", userUsageCount, coupon.getUsageLimitPerUser());

            if (userUsageCount >= coupon.getUsageLimitPerUser()) {
                log.warn("User has exceeded usage limit for coupon: {}", coupon.getCode());
                return CouponValidationResponse.builder()
                        .valid(false)
                        .message("Bạn đã sử dụng mã giảm giá này đủ số lần cho phép")
                        .build();
            }
        }

        // Check conditions
        Map<String, Object> conditions = convertJsonToConditions(coupon.getConditionsJson());
        log.info("Coupon conditions: {}", conditions);
        
        if (conditions != null) {
            // Check min spend
            if (conditions.containsKey("minSpend")) {
                BigDecimal minSpend = new BigDecimal(conditions.get("minSpend").toString());
                log.info("Checking min spend: {} vs order total: {}", minSpend, request.getOrderTotal());
                
                if (request.getOrderTotal().compareTo(minSpend) < 0) {
                    log.warn("Order total {} is less than min spend {}", request.getOrderTotal(), minSpend);
                    return CouponValidationResponse.builder()
                            .valid(false)
                            .message(String.format("Đơn hàng tối thiểu %s để sử dụng mã này", minSpend))
                            .build();
                }
            }
        }

        // Calculate discount
        BigDecimal discountAmount = calculateDiscount(coupon, request.getOrderTotal(), conditions);
        BigDecimal finalAmount = request.getOrderTotal().subtract(discountAmount);
        
        log.info("Coupon validation successful - Discount: {}, Final amount: {}", discountAmount, finalAmount);

        return CouponValidationResponse.builder()
                .valid(true)
                .message("Mã giảm giá hợp lệ")
                .couponId(coupon.getId())
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }

    @Transactional
    public void applyCoupon(UUID couponId, UUID userId, UUID orderId, BigDecimal discountAmount) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy coupon"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .orderId(orderId)
                .discountAmount(discountAmount)
                .build();

        couponUsageRepository.save(usage);

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        log.info("Applied coupon {} for user {} on order {}", coupon.getCode(), userId, orderId);
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal, Map<String, Object> conditions) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (coupon.getType()) {
            case PERCENTAGE:
                discount = orderTotal.multiply(coupon.getValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                // Check max discount
                if (conditions != null && conditions.containsKey("maxDiscount")) {
                    BigDecimal maxDiscount = new BigDecimal(conditions.get("maxDiscount").toString());
                    if (discount.compareTo(maxDiscount) > 0) {
                        discount = maxDiscount;
                    }
                }
                break;

            case FIXED:
                discount = coupon.getValue();
                if (discount.compareTo(orderTotal) > 0) {
                    discount = orderTotal;
                }
                break;

            case FREESHIP:
                // Free ship được xử lý riêng trong order service
                discount = coupon.getValue();
                break;
        }

        return discount;
    }

    private CouponResponse convertToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .type(coupon.getType())
                .value(coupon.getValue())
                .conditions(convertJsonToConditions(coupon.getConditionsJson()))
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .usageLimitPerUser(coupon.getUsageLimitPerUser())
                .status(coupon.getStatus())
                .startsAt(coupon.getStartsAt() != null
                        ? coupon.getStartsAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .expiresAt(coupon.getExpiresAt() != null
                        ? coupon.getExpiresAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .createdAt(coupon.getCreatedAt() != null
                        ? coupon.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .updatedAt(coupon.getUpdatedAt() != null
                        ? coupon.getUpdatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .isActive(coupon.isActive())
                .isExpired(coupon.isExpired())
                .showInBanner(coupon.getShowInBanner())
                .build();
    }

    private String convertConditionsToJson(Map<String, Object> conditions) {
        if (conditions == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(conditions);
        } catch (JsonProcessingException e) {
            log.error("Error converting conditions to JSON", e);
            return null;
        }
    }

    private Map<String, Object> convertJsonToConditions(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to conditions", e);
            return null;
        }
    }
}
