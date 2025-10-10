package com.mss301.kraft.coupon_service.controller;

import com.mss301.kraft.coupon_service.dto.*;
import com.mss301.kraft.coupon_service.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon Admin", description = "Coupon management APIs for Admin")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new coupon (Admin only)")
    public ResponseEntity<?> createCoupon(@Valid @RequestBody CouponRequest request) {
        try {
            CouponResponse response = couponService.createCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Exception sẽ được xử lý bởi GlobalExceptionHandler
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get coupon by ID (Admin only)")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable UUID id) {
        CouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all coupons (Admin only)")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> responses = couponService.getAllCoupons();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get paged coupons (Admin only)")
    public ResponseEntity<com.mss301.kraft.admin_service.dto.CouponPageResponse> getAllCouponsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(couponService.getAllCouponsPaged(page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update coupon (Admin only)")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable UUID id,
            @Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete coupon (Admin only)")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-code/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check if coupon code exists (Admin only)")
    public ResponseEntity<Map<String, Object>> checkCouponCode(@PathVariable String code) {
        boolean exists = couponService.couponCodeExists(code);
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("exists", exists);
        response.put("message", exists ? "Mã coupon đã tồn tại" : "Mã coupon có thể sử dụng");
        return ResponseEntity.ok(response);
    }
}
