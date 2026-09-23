package com.diego.customeraccount.account.infrastructure.out;

import com.diego.customeraccount.account.application.dto.AccountResponse;
import com.diego.customeraccount.account.domain.model.Account;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountJpaEntity toEntity(Account account) {
        return new AccountJpaEntity(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomerId(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }

    public static Account toDomain(AccountJpaEntity entity) {
        return Account.reconstitute(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getAccountType(),
                entity.getCurrency(),
                entity.getBalance(),
                entity.getStatus(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType().name(),
                account.getCurrency().name(),
                account.getBalance(),
                account.getStatus().name(),
                account.getCustomerId(),
                account.getCreatedAt(),
                account.getUpdatedAt());
    }
}