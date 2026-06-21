package com.divyanka.payment_gateway.controller;

import com.divyanka.payment_gateway.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.razorpay.Utils;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @PostMapping(value = "/razorpay", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        try {
            boolean valid = Utils.verifyWebhookSignature(
                payload, signature, webhookSecret);

            if (!valid) {
                return ResponseEntity.status(400).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }

        webhookService.processEvent(payload);
        return ResponseEntity.ok().build();
    }
}