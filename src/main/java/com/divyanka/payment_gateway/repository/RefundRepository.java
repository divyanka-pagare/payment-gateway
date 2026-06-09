package com.divyanka.payment_gateway.repository;

import com.divyanka.payment_gateway.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.payment.id = :paymentId")
    BigDecimal sumRefundedAmount(Long paymentId);

    Optional<Refund> findByRazorpayRefundId(String razorpayRefundId);
}