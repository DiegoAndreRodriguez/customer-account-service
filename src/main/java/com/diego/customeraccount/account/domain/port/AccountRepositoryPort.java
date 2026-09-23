package com.diego.customeraccount.account.domain.port;

import com.diego.customeraccount.account.domain.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    List<Account> findByCustomerId(UUID customerId);

    /** Devuelve el siguiente correlativo de la secuencia (RN-05). */
    long nextAccountNumberSequence();
}