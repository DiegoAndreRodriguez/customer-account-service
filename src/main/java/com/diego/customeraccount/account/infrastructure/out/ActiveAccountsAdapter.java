package com.diego.customeraccount.account.infrastructure.out;

import com.diego.customeraccount.account.domain.model.AccountStatus;
import com.diego.customeraccount.customer.domain.port.ActiveAccountsPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementa, desde el contexto de cuentas, el puerto que declara el contexto de clientes.
 * Permite cumplir RN-04 sin que 'customer' dependa de 'account'.
 */
@Component
public class ActiveAccountsAdapter implements ActiveAccountsPort {

    private final AccountJpaRepository jpaRepository;

    public ActiveAccountsAdapter(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsActiveAccountsForCustomer(UUID customerId) {
        return jpaRepository.existsByCustomerIdAndStatus(customerId, AccountStatus.ACTIVE);
    }
}