package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.request.CreateOrderRequest;
import com.divyanka.payment_gateway.dto.response.OrderResponse;
import com.divyanka.payment_gateway.entity.Order;
import com.divyanka.payment_gateway.entity.OrderStatus;
import com.divyanka.payment_gateway.entity.User;
import com.divyanka.payment_gateway.repository.OrderRepository;
import com.divyanka.payment_gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req) {

        // Idempotency check
        String idempotencyKey = req.getIdempotencyKey() != null
            ? req.getIdempotencyKey()
            : UUID.randomUUID().toString();

        orderRepository.findByIdempotencyKey(idempotencyKey)
            .ifPresent(o -> { throw new
                RuntimeException("Duplicate request - order already exists"); });

        // Get logged in user
        String email = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate unique order reference
        String orderRef = "ORD-"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Order order = Order.builder()
            .orderRef(orderRef)
            .user(user)
            .amount(req.getAmount())
            .currency(req.getCurrency() != null ? req.getCurrency() : "INR")
            .description(req.getDescription())
            .idempotencyKey(idempotencyKey)
            .status(OrderStatus.PENDING)
            .build();

        orderRepository.save(order);

        return OrderResponse.builder()
            .id(order.getId())
            .orderRef(order.getOrderRef())
            .amount(order.getAmount())
            .currency(order.getCurrency())
            .status(order.getStatus().name())
            .description(order.getDescription())
            .build();
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderResponse.builder()
            .id(order.getId())
            .orderRef(order.getOrderRef())
            .amount(order.getAmount())
            .currency(order.getCurrency())
            .status(order.getStatus().name())
            .description(order.getDescription())
            .build();
    }
}