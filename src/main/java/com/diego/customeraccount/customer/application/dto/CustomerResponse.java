package com.diego.customeraccount.customer.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}