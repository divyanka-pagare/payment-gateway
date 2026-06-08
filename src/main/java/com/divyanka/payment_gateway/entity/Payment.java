package com.divyanka.payment_gateway.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class Payment {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    @Column(unique = true) 
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;

    private String currency;

    private String method;

    private String errorCode;

    private String erroDescription;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Refund> refunds;

    @PrePersist
    public void prePersist() {
        initiatedAt = LocalDateTime.now();
        status = PaymentStatus.CREATED;
    }
}
