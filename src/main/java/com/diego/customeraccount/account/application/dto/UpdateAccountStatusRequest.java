package com.diego.customeraccount.account.application.dto;

import com.diego.customeraccount.account.domain.model.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountStatusRequest(

        @NotNull(message = "el estado es obligatorio")
        AccountStatus status
) {
}