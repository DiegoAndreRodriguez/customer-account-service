package com.diego.customeraccount.customer.domain.port;

import com.diego.customeraccount.customer.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida: el contrato de persistencia que necesita el dominio.
 * La implementacion vive en la capa de infraestructura.
 */
public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Page<Customer> findAll(Pageable pageable);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);
}