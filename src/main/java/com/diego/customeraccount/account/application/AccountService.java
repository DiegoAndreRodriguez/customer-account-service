package com.diego.customeraccount.account.application;

import com.diego.customeraccount.account.application.dto.AccountResponse;
import com.diego.customeraccount.account.application.dto.CreateAccountRequest;
import com.diego.customeraccount.account.application.dto.UpdateAccountStatusRequest;
import com.diego.customeraccount.account.domain.model.Account;
import com.diego.customeraccount.account.domain.port.AccountRepositoryPort;
import com.diego.customeraccount.account.infrastructure.out.AccountMapper;
import com.diego.customeraccount.customer.domain.model.Customer;
import com.diego.customeraccount.customer.domain.port.CustomerRepositoryPort;
import com.diego.customeraccount.shared.exception.BusinessRuleViolationException;
import com.diego.customeraccount.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.diego.customeraccount.account.domain.model.AccountStatus;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepositoryPort accountRepository;
    private final CustomerRepositoryPort customerRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepositoryPort accountRepository,
                          CustomerRepositoryPort customerRepository,
                          AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    /** CU-05: abrir una cuenta para un cliente existente y activo. */
    @Transactional
    public AccountResponse create(CreateAccountRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> ResourceNotFoundException.of("cliente", request.customerId()));

        // RN-06
        if (!customer.isActive()) {
            throw new BusinessRuleViolationException("RN-06",
                    "No se puede abrir una cuenta para un cliente inactivo");
        }

        String accountNumber = accountNumberGenerator.generate(
                request.accountType(), request.currency());

        Account account = Account.open(accountNumber, request.accountType(),
                request.currency(), customer.getId());

        return AccountMapper.toResponse(accountRepository.save(account));
    }

    /** CU-06: consultar una cuenta por su identificador. */
    @Transactional(readOnly = true)
    public AccountResponse findById(UUID id) {
        return AccountMapper.toResponse(loadOrFail(id));
    }

    /** CU-06: listar las cuentas de un cliente. */
    @Transactional(readOnly = true)
    public List<AccountResponse> findByCustomerId(UUID customerId) {

        if (customerRepository.findById(customerId).isEmpty()) {
            throw ResourceNotFoundException.of("cliente", customerId);
        }

        return accountRepository.findByCustomerId(customerId).stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    /** CU-07: cambiar el estado de una cuenta. */
    @Transactional
    public AccountResponse changeStatus(UUID id, UpdateAccountStatusRequest request) {
        Account account = loadOrFail(id);

        // RN-06: una cuenta solo se activa si su cliente está activo
        if (request.status() == AccountStatus.ACTIVE) {
            Customer customer = customerRepository.findById(account.getCustomerId())
                    .orElseThrow(() -> ResourceNotFoundException.of("cliente", account.getCustomerId()));

            if (!customer.isActive()) {
                throw new BusinessRuleViolationException("RN-06",
                        "No se puede activar una cuenta de un cliente inactivo");
            }
        }

        account.changeStatus(request.status());
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    private Account loadOrFail(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("cuenta", id));
    }
}
