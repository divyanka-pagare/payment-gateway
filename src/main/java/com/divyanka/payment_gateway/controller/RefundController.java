package com.divyanka.payment_gateway.controller;

import com.divyanka.payment_gateway.dto.request.RefundRequest;
import com.divyanka.payment_gateway.dto.response.RefundResponse;
import com.divyanka.payment_gateway.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundResponse> initiateRefund(
            @Valid @RequestBody RefundRequest req) {
        return ResponseEntity.ok(refundService.initiateRefund(req));
    }
}