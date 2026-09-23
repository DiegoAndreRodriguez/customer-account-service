package com.diego.customeraccount.customer.infrastructure.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);
}