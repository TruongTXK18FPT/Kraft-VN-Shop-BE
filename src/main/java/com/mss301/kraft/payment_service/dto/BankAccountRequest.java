package com.mss301.kraft.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {
    
    @NotBlank(message = "Tên ngân hàng không được để trống")
    private String bankName;
    
    @NotBlank(message = "Số tài khoản không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số tài khoản chỉ được chứa số")
    private String accountNumber;
    
    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String accountHolderName;
    
    private String imageUrl;
    
    private Boolean isActive;
}

