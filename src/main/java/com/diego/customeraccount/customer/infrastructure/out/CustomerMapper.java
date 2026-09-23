package com.diego.customeraccount.customer.infrastructure.out;

import com.diego.customeraccount.customer.application.dto.CustomerResponse;
import com.diego.customeraccount.customer.domain.model.Customer;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerJpaEntity toEntity(Customer customer) {
        return new CustomerJpaEntity(
                customer.getId(),
                customer.getDocumentNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }

    public static Customer toDomain(CustomerJpaEntity entity) {
        return Customer.reconstitute(
                entity.getId(),
                entity.getDocumentNumber(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getDocumentNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus().name(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }
}