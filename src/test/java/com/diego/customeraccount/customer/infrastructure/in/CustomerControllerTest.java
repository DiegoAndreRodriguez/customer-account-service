package com.diego.customeraccount.customer.infrastructure.in;

import com.diego.customeraccount.customer.application.CustomerService;
import com.diego.customeraccount.customer.application.dto.CustomerResponse;
import com.diego.customeraccount.shared.exception.BusinessRuleViolationException;
import com.diego.customeraccount.shared.exception.GlobalExceptionHandler;
import com.diego.customeraccount.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CustomerController(customerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST válido devuelve 201 con cabecera Location")
    void create_returnsCreatedWithLocation() throws Exception {
        UUID id = UUID.randomUUID();
        when(customerService.create(any())).thenReturn(sampleResponse(id));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "documentNumber": "71234567",
                                  "firstName": "Diego",
                                  "lastName": "Rodriguez",
                                  "email": "diego@example.com",
                                  "phone": "987654321"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/customers/" + id)))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST inválido devuelve 400 con el detalle de cada campo")
    void create_returnsBadRequestWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "documentNumber": "123",
                                  "firstName": "Diego",
                                  "lastName": "Rodriguez",
                                  "email": "no-es-un-correo"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)));

        verify(customerService, never()).create(any());
    }

    @Test
    @DisplayName("GET de un cliente inexistente devuelve 404")
    void findById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(customerService.findById(id)).thenThrow(ResourceNotFoundException.of("cliente", id));

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("DELETE que viola RN-04 devuelve 409 con el código de regla")
    void deactivate_returnsConflictWithRuleCode() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new BusinessRuleViolationException("RN-04", "Tiene cuentas activas"))
                .when(customerService).deactivate(id);

        mockMvc.perform(delete("/api/v1/customers/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.ruleCode").value("RN-04"));
    }

    private CustomerResponse sampleResponse(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        return new CustomerResponse(id, "71234567", "Diego", "Rodriguez",
                "diego@example.com", "987654321", "ACTIVE", now, now);
    }
}