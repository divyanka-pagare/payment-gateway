package com.divyanka.payment_gateway.entity;

public enum PaymentStatus {
    CREATED,
    INITIATED,
    AUTHORIZED,
    CAPTURED,
    SETTLED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}
