package com.diego.customeraccount.account.application;

import com.diego.customeraccount.account.domain.model.AccountType;
import com.diego.customeraccount.account.domain.model.Currency;
import com.diego.customeraccount.account.domain.port.AccountRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountNumberGeneratorTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @InjectMocks
    private AccountNumberGenerator generator;

    @ParameterizedTest(name = "{0} en {1} con correlativo {2} → {3}")
    @DisplayName("RN-05: genera el número con formato OOO-CC-NNNNNNNNN")
    @CsvSource({
            "SAVINGS,  PEN, 1,         00110000000001",
            "SAVINGS,  USD, 2,         00111000000002",
            "CHECKING, PEN, 3,         00120000000003",
            "CHECKING, USD, 123456789, 00121123456789"
    })
    void generate_buildsNumberWithExpectedFormat(AccountType type, Currency currency,
                                                 long sequence, String expected) {
        when(accountRepository.nextAccountNumberSequence()).thenReturn(sequence);

        String accountNumber = generator.generate(type, currency);

        assertThat(accountNumber).isEqualTo(expected).hasSize(14);
    }
}