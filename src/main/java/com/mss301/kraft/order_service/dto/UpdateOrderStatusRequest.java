package com.mss301.kraft.order_service.dto;

import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
}
