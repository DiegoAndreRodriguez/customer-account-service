package com.diego.customeraccount.customer.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio. No depende de JPA ni de ningun framework:
 * representa al cliente y protege su estado valido.
 */
public class Customer {

    private final UUID id;
    private final String documentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private CustomerStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Customer(UUID id, String documentNumber, String firstName, String lastName,
                     String email, String phone, CustomerStatus status,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Alta de un cliente nuevo. Nace siempre ACTIVE. */
    public static Customer register(String documentNumber, String firstName,
                                    String lastName, String email, String phone) {
        LocalDateTime now = LocalDateTime.now();
        return new Customer(UUID.randomUUID(), documentNumber, firstName, lastName,
                email, phone, CustomerStatus.ACTIVE, now, now);
    }

    /** Reconstrucción desde persistencia. Uso exclusivo de los adaptadores de salida. */
    public static Customer reconstitute(UUID id, String documentNumber, String firstName,
                                        String lastName, String email, String phone,
                                        CustomerStatus status, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        return new Customer(id, documentNumber, firstName, lastName, email, phone,
                status, createdAt, updatedAt);
    }

    /** RN-08: el DNI es inmutable; los datos de contacto, incluido el correo, se pueden actualizar. */
    public void updateContactInfo(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    /** RN-03: la baja es lógica, nunca física. */
    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /** CU-08: reactivación explícita de un cliente inactivo. */
    public void reactivate() {
        this.status = CustomerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == CustomerStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public String getDocumentNumber() { return documentNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public CustomerStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}