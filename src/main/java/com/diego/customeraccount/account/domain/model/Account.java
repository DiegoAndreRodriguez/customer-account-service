package com.diego.customeraccount.account.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {

    private final UUID id;
    private final String accountNumber;
    private final AccountType accountType;
    private final Currency currency;
    private final BigDecimal balance;
    private AccountStatus status;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Account(UUID id, String accountNumber, AccountType accountType, Currency currency,
                    BigDecimal balance, AccountStatus status, UUID customerId,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** RN-07: toda cuenta nueva nace con saldo 0.00 y estado ACTIVE. */
    public static Account open(String accountNumber, AccountType accountType,
                               Currency currency, UUID customerId) {
        LocalDateTime now = LocalDateTime.now();
        return new Account(UUID.randomUUID(), accountNumber, accountType, currency,
                BigDecimal.ZERO.setScale(2), AccountStatus.ACTIVE, customerId, now, now);
    }

    /** Reconstrucción desde persistencia. Uso exclusivo de los adaptadores de salida. */
    public static Account reconstitute(UUID id, String accountNumber, AccountType accountType,
                                       Currency currency, BigDecimal balance, AccountStatus status,
                                       UUID customerId, LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return new Account(id, accountNumber, accountType, currency, balance,
                status, customerId, createdAt, updatedAt);
    }

    /** CU-07: cambio de estado de la cuenta. */
    public void changeStatus(AccountStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public UUID getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}