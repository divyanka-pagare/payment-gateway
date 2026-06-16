package com.divyanka.payment_gateway.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderRef;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
}