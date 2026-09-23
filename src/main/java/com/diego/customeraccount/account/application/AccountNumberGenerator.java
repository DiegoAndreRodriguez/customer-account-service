package com.diego.customeraccount.account.application;

import com.diego.customeraccount.account.domain.model.AccountType;
import com.diego.customeraccount.account.domain.model.Currency;
import com.diego.customeraccount.account.domain.port.AccountRepositoryPort;
import org.springframework.stereotype.Component;

/**
 * RN-05: construye el número de cuenta con el formato OOO-CC-NNNNNNNNN.
 * OOO: codigo de oficina. CC: tipo de cuenta y moneda. NNNNNNNNN: correlativo.
 */
@Component
public class AccountNumberGenerator {

    private static final String OFFICE_CODE = "001";

    private final AccountRepositoryPort accountRepository;

    public AccountNumberGenerator(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generate(AccountType accountType, Currency currency) {
        String productCode = buildProductCode(accountType, currency);
        long sequence = accountRepository.nextAccountNumberSequence();
        return OFFICE_CODE + productCode + "%09d".formatted(sequence);
    }

    private String buildProductCode(AccountType accountType, Currency currency) {
        int base = Integer.parseInt(accountType.getCode());
        return "%02d".formatted(base + currency.getOffset());
    }
}