package com.diego.customeraccount.customer.domain.port;

import java.util.UUID;

/**
 * Puerto de salida que el dominio de clientes necesita para cumplir RN-04.
 * El contexto de cuentas lo implementa: así 'customer' nunca depende de 'account'.
 */
public interface ActiveAccountsPort {

    boolean existsActiveAccountsForCustomer(UUID customerId);
}