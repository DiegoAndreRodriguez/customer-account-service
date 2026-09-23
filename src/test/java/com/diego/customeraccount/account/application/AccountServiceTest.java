package com.diego.customeraccount.account.application;

import com.diego.customeraccount.account.application.dto.AccountResponse;
import com.diego.customeraccount.account.application.dto.CreateAccountRequest;
import com.diego.customeraccount.account.application.dto.UpdateAccountStatusRequest;
import com.diego.customeraccount.account.domain.model.Account;
import com.diego.customeraccount.account.domain.model.AccountStatus;
import com.diego.customeraccount.account.domain.model.AccountType;
import com.diego.customeraccount.account.domain.model.Currency;
import com.diego.customeraccount.account.domain.port.AccountRepositoryPort;
import com.diego.customeraccount.customer.domain.model.Customer;
import com.diego.customeraccount.customer.domain.port.CustomerRepositoryPort;
import com.diego.customeraccount.shared.exception.BusinessRuleViolationException;
import com.diego.customeraccount.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("CU-05 / RN-07: abre una cuenta con saldo 0.00 y estado ACTIVE")
    void create_opensAccountWithZeroBalance() {
        Customer customer = activeCustomer();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(accountNumberGenerator.generate(AccountType.SAVINGS, Currency.PEN))
                .thenReturn("00110000000001");
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse response = accountService.create(
                new CreateAccountRequest(customer.getId(), AccountType.SAVINGS, Currency.PEN));

        assertThat(response.accountNumber()).isEqualTo("00110000000001");
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.customerId()).isEqualTo(customer.getId());
    }

    @Test
    @DisplayName("RN-06: lanza 404 si el cliente no existe")
    void create_throwsWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.create(
                new CreateAccountRequest(customerId, AccountType.SAVINGS, Currency.PEN)))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(accountNumberGenerator);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("RN-06: impide abrir una cuenta a un cliente inactivo")
    void create_rejectsInactiveCustomer() {
        Customer customer = activeCustomer();
        customer.deactivate();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> accountService.create(
                new CreateAccountRequest(customer.getId(), AccountType.CHECKING, Currency.USD)))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasFieldOrPropertyWithValue("ruleCode", "RN-06");

        verifyNoInteractions(accountNumberGenerator);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU-07: cambia el estado de una cuenta")
    void changeStatus_updatesAccountStatus() {
        Account account = Account.open(
                "00110000000001", AccountType.SAVINGS, Currency.PEN, UUID.randomUUID());
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse response = accountService.changeStatus(
                account.getId(), new UpdateAccountStatusRequest(AccountStatus.INACTIVE));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    private Customer activeCustomer() {
        return Customer.register(
                "71234567", "Diego", "Rodriguez", "diego@example.com", "987654321");
    }
}