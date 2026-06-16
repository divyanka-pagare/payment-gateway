package com.divyanka.payment_gateway.entity;

import jakarta.persistence.*; // database mapping (JPA)
import lombok.*;    // reduces boilerplate code
import java.math.BigDecimal;   // precise money handling
import java.time.LocalDateTime;  // date and time 

@Entity   // this class is database table
@Table(name = "orders")


// this are from Lombok
@Data  //getters/setters/toString
@Builder  // easy object creation
@NoArgsConstructor  // empty constructor
@AllArgsConstructor  // full constructor

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(unique = true)
    private String idempotencyKey;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        status = OrderStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
