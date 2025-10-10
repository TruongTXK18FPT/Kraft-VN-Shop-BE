package com.mss301.kraft.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private UUID id;
    private String province;
    private String district;
    private String ward;
    private String line1;
    private boolean defaultAddress;
}
