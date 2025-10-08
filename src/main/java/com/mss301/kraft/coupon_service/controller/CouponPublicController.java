package com.mss301.kraft.coupon_service.controller;

import com.mss301.kraft.coupon_service.dto.*;
import com.mss301.kraft.coupon_service.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon Public", description = "Public coupon APIs")
public class CouponPublicController {

    private final CouponService couponService;

    @GetMapping("/code/{code}")
    @Operation(summary = "Get coupon by code")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String code) {
        CouponResponse response = couponService.getCouponByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active coupons")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        List<CouponResponse> responses = couponService.getActiveCoupons();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate coupon code")
    public ResponseEntity<CouponValidationResponse> validateCoupon(
            @Valid @RequestBody ValidateCouponRequest request) {
        CouponValidationResponse response = couponService.validateCoupon(request);
        return ResponseEntity.ok(response);
    }
}
