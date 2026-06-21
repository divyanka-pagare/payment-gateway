package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.request.RefundRequest;
import com.divyanka.payment_gateway.dto.response.RefundResponse;
import com.divyanka.payment_gateway.entity.Payment;
import com.divyanka.payment_gateway.entity.PaymentStatus;
import com.divyanka.payment_gateway.entity.Refund;
import com.divyanka.payment_gateway.repository.PaymentRepository;
import com.divyanka.payment_gateway.repository.RefundRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    @Transactional
    public RefundResponse initiateRefund(RefundRequest req) {
        Payment payment = paymentRepository
            .findByRazorpayPaymentId(req.getPaymentId())
            .orElseThrow(() -> new RuntimeException(
                "Payment not found: " + req.getPaymentId()));

        BigDecimal totalRefunded = refundRepository
            .sumRefundedAmount(payment.getId());
        BigDecimal available = payment.getAmount().subtract(totalRefunded);

        if (req.getAmount().compareTo(available) > 0) {
            throw new RuntimeException(
                "Refund amount exceeds available: " + available);
        }

        try {
            int amountPaise = req.getAmount()
                .multiply(BigDecimal.valueOf(100)).intValue();

            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);

            com.razorpay.Refund rzpRefund =
                razorpayClient.payments.refund(req.getPaymentId(), options);

            Refund refund = Refund.builder()
                .payment(payment)
                .razorpayRefundId(rzpRefund.get("id"))
                .amount(req.getAmount())
                .reason(req.getReason())
                .status("pending")
                .createdAt(LocalDateTime.now())
                .build();

            refundRepository.save(refund);

            boolean isFullRefund = req.getAmount()
                .compareTo(payment.getAmount()) == 0;
            payment.setStatus(isFullRefund
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED);
            paymentRepository.save(payment);

            return RefundResponse.builder()
                .refundId(refund.getRazorpayRefundId())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .build();

        } catch (RazorpayException e) {
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }
}