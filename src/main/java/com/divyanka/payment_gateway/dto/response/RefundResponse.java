package com.divyanka.payment_gateway.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RefundResponse {
    private String refundId;
    private BigDecimal amount;
    private String status;
}