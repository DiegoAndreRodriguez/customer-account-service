package com.diego.customeraccount.account.infrastructure.out;

import com.diego.customeraccount.account.domain.model.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {

    List<AccountJpaEntity> findByCustomerId(UUID customerId);

    boolean existsByCustomerIdAndStatus(UUID customerId, AccountStatus status);

    @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
    long nextAccountNumberSequence();
}