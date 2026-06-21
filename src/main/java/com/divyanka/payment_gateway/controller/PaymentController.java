package com.divyanka.payment_gateway.controller;

import com.divyanka.payment_gateway.dto.request.VerifyPaymentRequest;
import com.divyanka.payment_gateway.dto.response.CreatePaymentResponse;
import com.divyanka.payment_gateway.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.createPaymentOrder(orderId));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest req) {
        return ResponseEntity.ok(paymentService.verifyAndCapture(req));
    }
}