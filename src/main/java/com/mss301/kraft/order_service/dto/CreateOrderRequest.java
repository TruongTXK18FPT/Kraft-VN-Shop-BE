package com.mss301.kraft.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name must not exceed 100 characters")
    private String recipientName;

    @NotBlank(message = "Recipient phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String recipientPhone;

    @NotBlank(message = "Recipient address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String recipientAddress;

    @NotBlank(message = "Ward is required")
    private String recipientWard;

    @NotBlank(message = "District is required")
    private String recipientDistrict;

    @NotBlank(message = "Province is required")
    private String recipientProvince;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(bank_transfer|cod)$", message = "Payment method must be 'bank_transfer' or 'cod'")
    private String paymentMethod;

    @NotNull(message = "Shipping fee is required")
    private BigDecimal shippingFee;

    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    private String couponCode; // Optional coupon code
}
