package com.diego.customeraccount.account.infrastructure.out;

import com.diego.customeraccount.account.domain.model.Account;
import com.diego.customeraccount.account.domain.port.AccountRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryAdapter(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        return AccountMapper.toDomain(jpaRepository.save(AccountMapper.toEntity(account)));
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(AccountMapper::toDomain)
                .toList();
    }

    @Override
    public long nextAccountNumberSequence() {
        return jpaRepository.nextAccountNumberSequence();
    }
}