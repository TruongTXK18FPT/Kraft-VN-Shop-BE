package com.mss301.kraft.coupon_service.exception;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String message) {
        super(message);
    }
    
    public CouponNotFoundException(UUID id) {
        super("Không tìm thấy coupon với id: " + id);
    }
    
    public CouponNotFoundException(String code, boolean isCode) {
        super("Không tìm thấy coupon với mã: " + code);
    }
}
