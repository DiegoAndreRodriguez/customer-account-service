package com.diego.customeraccount.customer.infrastructure.in;

import com.diego.customeraccount.customer.application.CustomerService;
import com.diego.customeraccount.customer.application.dto.CreateCustomerRequest;
import com.diego.customeraccount.customer.application.dto.CustomerResponse;
import com.diego.customeraccount.customer.application.dto.UpdateCustomerRequest;
import com.diego.customeraccount.shared.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Gestión de clientes del banco")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Registra un cliente nuevo (CU-01)")
    @ApiResponse(responseCode = "201", description = "Cliente registrado")
    @ApiResponse(responseCode = "400", description = "Datos con formato inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Documento o correo ya registrado (RN-01, RN-02)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request,
            UriComponentsBuilder uriBuilder) {

        CustomerResponse response = customerService.create(request);

        return ResponseEntity
                .created(uriBuilder.path("/api/v1/customers/{id}")
                        .buildAndExpand(response.id()).toUri())
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Lista los clientes de forma paginada (CU-02)")
    @ApiResponse(responseCode = "200", description = "Página de clientes")
    public ResponseEntity<Page<CustomerResponse>> findAll(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(customerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un cliente por su identificador (CU-02)")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza los datos de contacto de un cliente (CU-03)")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado")
    @ApiResponse(responseCode = "409", description = "El correo pertenece a otro cliente (RN-02)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos con formato inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Da de baja lógica a un cliente (CU-04)")
    @ApiResponse(responseCode = "204", description = "Cliente inactivado")
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El cliente tiene cuentas activas (RN-04)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        customerService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactivation")
    @Operation(summary = "Reactiva un cliente inactivo (CU-08)")
    @ApiResponse(responseCode = "200", description = "Cliente reactivado")
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El cliente ya está activo (RN-09)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CustomerResponse> reactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.reactivate(id));
    }
}