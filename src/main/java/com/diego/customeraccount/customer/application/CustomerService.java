package com.diego.customeraccount.customer.application;
import com.diego.customeraccount.customer.domain.port.ActiveAccountsPort;
import com.diego.customeraccount.shared.exception.BusinessRuleViolationException;

import com.diego.customeraccount.customer.application.dto.CreateCustomerRequest;
import com.diego.customeraccount.customer.application.dto.CustomerResponse;
import com.diego.customeraccount.customer.application.dto.UpdateCustomerRequest;
import com.diego.customeraccount.customer.domain.model.Customer;
import com.diego.customeraccount.customer.domain.port.CustomerRepositoryPort;
import com.diego.customeraccount.customer.infrastructure.out.CustomerMapper;
import com.diego.customeraccount.shared.exception.DuplicateResourceException;
import com.diego.customeraccount.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepositoryPort customerRepository;
    private final ActiveAccountsPort activeAccountsPort;

    public CustomerService(CustomerRepositoryPort customerRepository,
                           ActiveAccountsPort activeAccountsPort) {
        this.customerRepository = customerRepository;
        this.activeAccountsPort = activeAccountsPort;
    }

    /** CU-01: registrar un cliente. */
    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {

        if (customerRepository.existsByDocumentNumber(request.documentNumber())) {
            throw DuplicateResourceException.of("nomero de documento", request.documentNumber());
        }
        if (customerRepository.existsByEmail(request.email())) {
            throw DuplicateResourceException.of("correo electronico", request.email());
        }

        Customer customer = Customer.register(
                request.documentNumber(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone());

        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    /** CU-02: consultar un cliente por su identificador. */
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return CustomerMapper.toResponse(loadOrFail(id));
    }

    /** CU-02: listar clientes de forma paginada. */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerMapper::toResponse);
    }

    /** CU-03: actualizar los datos de contacto. */
    @Transactional
    public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
        Customer customer = loadOrFail(id);

        // RN-02: si el correo cambia, no puede pertenecer a otro cliente
        boolean emailChanged = !customer.getEmail().equals(request.email());
        if (emailChanged && customerRepository.existsByEmail(request.email())) {
            throw DuplicateResourceException.of("correo electrónico", request.email());
        }

        customer.updateContactInfo(request.firstName(), request.lastName(),
                request.email(), request.phone());
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    /** CU-04: baja logica del cliente. */
    @Transactional
    public void deactivate(UUID id) {
        Customer customer = loadOrFail(id);
        if (activeAccountsPort.existsActiveAccountsForCustomer(id)) {
            throw new BusinessRuleViolationException("RN-04",
                    "No se puede inactivar el cliente porque tiene cuentas activas");
        }
        // RN-04 (validación de cuentas activas) se incorpora al construir el dominio Account.
        customer.deactivate();
        customerRepository.save(customer);
    }
    /** CU-08: reactivar un cliente inactivo. */
    @Transactional
    public CustomerResponse reactivate(UUID id) {
        Customer customer = loadOrFail(id);

        if (customer.isActive()) {
            throw new BusinessRuleViolationException("RN-09",
                    "El cliente ya se encuentra activo");
        }

        customer.reactivate();
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    private Customer loadOrFail(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("cliente", id));
    }
}