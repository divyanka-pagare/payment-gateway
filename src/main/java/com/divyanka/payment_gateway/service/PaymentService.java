package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.request.VerifyPaymentRequest;
import com.divyanka.payment_gateway.dto.response.CreatePaymentResponse;
import com.divyanka.payment_gateway.entity.Order;
import com.divyanka.payment_gateway.entity.Payment;
import com.divyanka.payment_gateway.entity.PaymentStatus;
import com.divyanka.payment_gateway.repository.OrderRepository;
import com.divyanka.payment_gateway.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Transactional
    public CreatePaymentResponse createPaymentOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException(
                "Order not found: " + orderId));

        try {
            int amountInPaise = order.getAmount()
                .multiply(BigDecimal.valueOf(100)).intValue();

            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", order.getCurrency());
            options.put("receipt", order.getOrderRef());

            com.razorpay.Order rzpOrder =
                razorpayClient.orders.create(options);

            Payment payment = Payment.builder()
                .order(order)
                .razorpayOrderId(rzpOrder.get("id"))
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .status(PaymentStatus.INITIATED)
                .initiatedAt(LocalDateTime.now())
                .build();

            paymentRepository.save(payment);

            return CreatePaymentResponse.builder()
                .razorpayOrderId(rzpOrder.get("id"))
                .amount(amountInPaise)
                .currency(order.getCurrency())
                .build();

        } catch (RazorpayException e) {
            throw new RuntimeException("Razorpay error: " + e.getMessage());
        }
    }

    @Transactional
    public String verifyAndCapture(VerifyPaymentRequest req) {
        String payload = req.getOrderId() + "|" + req.getPaymentId();

        boolean isValid;
        try {
            isValid = Utils.verifySignature(payload,
                req.getSignature(), keySecret);
        } catch (RazorpayException e) {
            throw new RuntimeException("Signature verification failed");
        }

        if (!isValid) {
            throw new RuntimeException("Invalid payment signature");
        }

        Payment payment = paymentRepository
            .findByRazorpayOrderId(req.getOrderId())
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setRazorpayPaymentId(req.getPaymentId());
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return "Payment verified and captured successfully";
    }
}