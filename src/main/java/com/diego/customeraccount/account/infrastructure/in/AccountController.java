package com.diego.customeraccount.account.infrastructure.in;

import com.diego.customeraccount.account.application.AccountService;
import com.diego.customeraccount.account.application.dto.AccountResponse;
import com.diego.customeraccount.account.application.dto.CreateAccountRequest;
import com.diego.customeraccount.account.application.dto.UpdateAccountStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<AccountResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/api/v1/customers/{customerId}/accounts")
    @Operation(summary = "Lista las cuentas de un cliente (CU-06)")
    public ResponseEntity<List<AccountResponse>> findByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(accountService.findByCustomerId(customerId));
    }

    @PatchMapping("/api/v1/accounts/{id}/status")
    @Operation(summary = "Cambia el estado de una cuenta (CU-07)")
    public ResponseEntity<AccountResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountStatusRequest request) {

        return ResponseEntity.ok(accountService.changeStatus(id, request));
    }
}