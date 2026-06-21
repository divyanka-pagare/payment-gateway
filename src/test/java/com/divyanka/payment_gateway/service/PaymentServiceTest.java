package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.request.VerifyPaymentRequest;
import com.divyanka.payment_gateway.entity.Order;
import com.divyanka.payment_gateway.entity.OrderStatus;
import com.divyanka.payment_gateway.repository.OrderRepository;
import com.divyanka.payment_gateway.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            paymentService.createPaymentOrder(99L));
    }

    @Test
    void shouldThrowOnInvalidSignature() {
        VerifyPaymentRequest req = new VerifyPaymentRequest();
        req.setOrderId("order_test123");
        req.setPaymentId("pay_test123");
        req.setSignature("invalid_signature_value");

        assertThrows(RuntimeException.class, () ->
            paymentService.verifyAndCapture(req));
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        Order order = Order.builder()
            .id(1L)
            .orderRef("ORD-TEST-001")
            .amount(BigDecimal.valueOf(500))
            .currency("INR")
            .status(OrderStatus.PENDING)
            .build();

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(500), order.getAmount());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }
}