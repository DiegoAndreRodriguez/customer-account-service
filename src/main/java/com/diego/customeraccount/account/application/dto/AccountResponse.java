package com.diego.customeraccount.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        String accountType,
        String currency,
        BigDecimal balance,
        String status,
        UUID customerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}