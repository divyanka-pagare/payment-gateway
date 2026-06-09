package com.divyanka.payment_gateway.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReportData {
    private LocalDate date;
    private int totalTransactions;
    private BigDecimal totalCaptured;
    private BigDecimal totalRefunded;
    private BigDecimal netSettled;
    private long failedCount;
}