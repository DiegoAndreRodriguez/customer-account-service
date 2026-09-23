package com.diego.customeraccount.customer.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @NotBlank(message = "el numero de documento es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "debe tener exactamente 8 dígitos")
        String documentNumber,

        @NotBlank(message = "los nombres son obligatorios")
        @Size(max = 100, message = "no puede exceder 100 caracteres")
        String firstName,

        @NotBlank(message = "los apellidos son obligatorios")
        @Size(max = 100, message = "no puede exceder 100 caracteres")
        String lastName,

        @NotBlank(message = "el correo es obligatorio")
        @Email(message = "debe ser una direccion de correo valida")
        @Size(max = 150, message = "no puede exceder 150 caracteres")
        String email,

        @Pattern(regexp = "\\d{6,15}|", message = "debe contener entre 6 y 15 digitos")
        String phone
) {
}