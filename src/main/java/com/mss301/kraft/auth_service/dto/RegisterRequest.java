package com.mss301.kraft.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    // Required address fields
    @NotBlank
    private String province;

    // Optional address fields
    private String district;
    private String ward;
    private String line1;
}
