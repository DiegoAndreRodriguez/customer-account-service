package com.diego.customeraccount.customer.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "los nombres son obligatorios")
        @Size(max = 100, message = "no puede exceder 100 caracteres")
        String firstName,

        @NotBlank(message = "los apellidos son obligatorios")
        @Size(max = 100, message = "no puede exceder 100 caracteres")
        String lastName,

        @Pattern(regexp = "\\d{6,15}|", message = "debe contener entre 6 y 15 dígitos")
        String phone
) {
}