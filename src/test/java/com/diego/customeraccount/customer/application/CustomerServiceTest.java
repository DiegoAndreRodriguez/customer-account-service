package com.diego.customeraccount.customer.application;

import com.diego.customeraccount.customer.application.dto.CreateCustomerRequest;
import com.diego.customeraccount.customer.application.dto.CustomerResponse;
import com.diego.customeraccount.customer.domain.model.Customer;
import com.diego.customeraccount.customer.domain.model.CustomerStatus;
import com.diego.customeraccount.customer.domain.port.ActiveAccountsPort;
import com.diego.customeraccount.customer.domain.port.CustomerRepositoryPort;
import com.diego.customeraccount.shared.exception.BusinessRuleViolationException;
import com.diego.customeraccount.shared.exception.DuplicateResourceException;
import com.diego.customeraccount.shared.exception.ResourceNotFoundException;
import com.diego.customeraccount.customer.application.dto.UpdateCustomerRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private ActiveAccountsPort activeAccountsPort;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("CU-01: registra un cliente nuevo en estado ACTIVE")
    void create_registersActiveCustomer() {
        when(customerRepository.existsByDocumentNumber("71234567")).thenReturn(false);
        when(customerRepository.existsByEmail("diego@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse response = customerService.create(validRequest());

        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.documentNumber()).isEqualTo("71234567");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("RN-01: rechaza un número de documento duplicado")
    void create_rejectsDuplicateDocument() {
        when(customerRepository.existsByDocumentNumber("71234567")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(validRequest()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-02: rechaza un correo electrónico duplicado")
    void create_rejectsDuplicateEmail() {
        when(customerRepository.existsByDocumentNumber("71234567")).thenReturn(false);
        when(customerRepository.existsByEmail("diego@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(validRequest()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-04: impide inactivar un cliente con cuentas activas")
    void deactivate_rejectsWhenCustomerHasActiveAccounts() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(activeAccountsPort.existsActiveAccountsForCustomer(customer.getId())).thenReturn(true);

        assertThatThrownBy(() -> customerService.deactivate(customer.getId()))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasFieldOrPropertyWithValue("ruleCode", "RN-04");

        assertThat(customer.isActive()).isTrue();
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-03: la baja es lógica, el cliente queda INACTIVE")
    void deactivate_marksCustomerAsInactive() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(activeAccountsPort.existsActiveAccountsForCustomer(customer.getId())).thenReturn(false);

        customerService.deactivate(customer.getId());

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("CU-02: lanza 404 si el cliente no existe")
    void findById_throwsWhenCustomerDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("RN-08: permite actualizar el correo si no pertenece a otro cliente")
    void update_allowsChangingEmail() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse response = customerService.update(customer.getId(),
                new UpdateCustomerRequest("Diego", "Rodriguez", "nuevo@example.com", "987654321"));

        assertThat(response.email()).isEqualTo("nuevo@example.com");
        assertThat(response.documentNumber()).isEqualTo("71234567");
    }

    @Test
    @DisplayName("RN-02: rechaza cambiar el correo por uno de otro cliente")
    void update_rejectsEmailOfAnotherCustomer() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("ocupado@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.update(customer.getId(),
                new UpdateCustomerRequest("Diego", "Rodriguez", "ocupado@example.com", "987654321")))
                .isInstanceOf(DuplicateResourceException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-02: conservar el mismo correo no se considera duplicado")
    void update_keepsSameEmailWithoutDuplicateCheck() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        customerService.update(customer.getId(),
                new UpdateCustomerRequest("Diego Andre", "Rodriguez", "diego@example.com", "999888777"));

        verify(customerRepository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("CU-08: reactiva un cliente inactivo")
    void reactivate_setsCustomerAsActive() {
        Customer customer = activeCustomer();
        customer.deactivate();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse response = customerService.reactivate(customer.getId());

        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("RN-09: rechaza reactivar un cliente que ya está activo")
    void reactivate_rejectsActiveCustomer() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> customerService.reactivate(customer.getId()))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasFieldOrPropertyWithValue("ruleCode", "RN-09");

        verify(customerRepository, never()).save(any());
    }

    private CreateCustomerRequest validRequest() {
        return new CreateCustomerRequest(
                "71234567", "Diego", "Rodriguez", "diego@example.com", "987654321");
    }

    private Customer activeCustomer() {
        return Customer.register(
                "71234567", "Diego", "Rodriguez", "diego@example.com", "987654321");
    }
}