package com.diego.customeraccount.account.application.dto;

import com.diego.customeraccount.account.domain.model.AccountType;
import com.diego.customeraccount.account.domain.model.Currency;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(

        @NotNull(message = "el identificador del cliente es obligatorio")
        UUID customerId,

        @NotNull(message = "el tipo de cuenta es obligatorio")
        AccountType accountType,

        @NotNull(message = "la moneda es obligatoria")
        Currency currency
) {
}