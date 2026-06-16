package com.divyanka.payment_gateway.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    private String currency;
    private String description;
    private String idempotencyKey;
}