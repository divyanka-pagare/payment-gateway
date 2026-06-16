package com.divyanka.payment_gateway.repository;

import com.divyanka.payment_gateway.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderRef(String orderRef);
    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}