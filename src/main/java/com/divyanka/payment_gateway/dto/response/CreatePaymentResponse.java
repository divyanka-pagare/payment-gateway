package com.divyanka.payment_gateway.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentResponse {
    private String razorpayOrderId;
    private int amount;
    private String currency;
}