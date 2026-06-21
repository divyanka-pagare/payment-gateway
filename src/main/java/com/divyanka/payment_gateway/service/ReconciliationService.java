package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.response.ReportData;
import com.divyanka.payment_gateway.entity.Payment;
import com.divyanka.payment_gateway.entity.PaymentStatus;
import com.divyanka.payment_gateway.entity.Refund;
import com.divyanka.payment_gateway.repository.PaymentRepository;
import com.divyanka.payment_gateway.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final PaymentRepository paymentRepository;
    // private final RefundRepository refundRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyReport() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        ReportData report = buildReport(yesterday);
        log.info("Daily reconciliation for {}: {}", yesterday, report);
    }

    public ReportData buildReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Payment> payments =
            paymentRepository.findByCompletedAtBetween(start, end);

        BigDecimal totalCaptured = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.CAPTURED
                || p.getStatus() == PaymentStatus.SETTLED)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = payments.stream()
            .flatMap(p -> p.getRefunds().stream())
            .map(Refund::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long failedCount = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.FAILED)
            .count();

        return ReportData.builder()
            .date(date)
            .totalTransactions(payments.size())
            .totalCaptured(totalCaptured)
            .totalRefunded(totalRefunded)
            .netSettled(totalCaptured.subtract(totalRefunded))
            .failedCount(failedCount)
            .build();
    }
}