package com.diego.customeraccount.customer.infrastructure.out;

import com.diego.customeraccount.customer.domain.model.Customer;
import com.diego.customeraccount.customer.domain.port.CustomerRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa el puerto del dominio usando Spring Data JPA.
 * Es la única clase que conoce, a la vez, el modelo de dominio y el de persistencia.
 */
@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity saved = jpaRepository.save(CustomerMapper.toEntity(customer));
        return CustomerMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(CustomerMapper::toDomain);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(CustomerMapper::toDomain);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}