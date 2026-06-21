package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.entity.PaymentStatus;
import com.divyanka.payment_gateway.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void processEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.get("event").asText();

            switch (event) {
                case "payment.captured" -> handleCaptured(root.get("payload"));
                case "payment.failed" -> handleFailed(root.get("payload"));
                default -> log.info("Unhandled webhook event: {}", event);
            }
        } catch (Exception e) {
            log.error("Webhook processing error", e);
        }
    }

    private void handleCaptured(JsonNode payload) {
        String paymentId = payload.get("payment").get("entity").get("id").asText();
        String orderId = payload.get("payment").get("entity").get("order_id").asText();

        paymentRepository.findByRazorpayOrderId(orderId)
            .ifPresent(p -> {
                p.setStatus(PaymentStatus.CAPTURED);
                p.setRazorpayPaymentId(paymentId);
                p.setCompletedAt(LocalDateTime.now());
                paymentRepository.save(p);
            });
    }

    private void handleFailed(JsonNode payload) {
        String orderId = payload.get("payment").get("entity").get("order_id").asText();

        paymentRepository.findByRazorpayOrderId(orderId)
            .ifPresent(p -> {
                p.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(p);
            });
    }
}