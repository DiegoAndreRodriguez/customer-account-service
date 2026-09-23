package com.diego.customeraccount.account.infrastructure.in;

import com.diego.customeraccount.account.application.AccountService;
import com.diego.customeraccount.account.application.dto.AccountResponse;
import com.diego.customeraccount.account.application.dto.CreateAccountRequest;
import com.diego.customeraccount.account.application.dto.UpdateAccountStatusRequest;
import com.diego.customeraccount.shared.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Accounts", description = "Gestión de cuentas bancarias")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/api/v1/accounts")
    @Operation(summary = "Abre una cuenta para un cliente existente (CU-05)")
    @ApiResponse(responseCode = "201", description = "Cuenta abierta con saldo 0.00")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o valor de enumerado no permitido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El cliente está inactivo (RN-06)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest request,
            UriComponentsBuilder uriBuilder) {

        AccountResponse response = accountService.create(request);

        return ResponseEntity
                .created(uriBuilder.path("/api/v1/accounts/{id}")
                        .buildAndExpand(response.id()).toUri())
                .body(response);
    }

    @GetMapping("/api/v1/accounts/{id}")
    @Operation(summary = "Obtiene una cuenta por su identificador (CU-06)")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada")
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AccountResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/api/v1/customers/{customerId}/accounts")
    @Operation(summary = "Lista las cuentas de un cliente (CU-06)")
    @ApiResponse(responseCode = "200", description = "Cuentas del cliente")
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<AccountResponse>> findByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(accountService.findByCustomerId(customerId));
    }

    @PatchMapping("/api/v1/accounts/{id}/status")
    @Operation(summary = "Cambia el estado de una cuenta (CU-07)")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "400", description = "Estado no válido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AccountResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountStatusRequest request) {

        return ResponseEntity.ok(accountService.changeStatus(id, request));
    }
}