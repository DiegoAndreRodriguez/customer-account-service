package com.diego.customeraccount.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerAccountOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Customer Account Service API")
                .version("1.0.0")
                .description("Servicio de gestión de clientes y cuentas bancarias. "
                        + "Implementa arquitectura hexagonal y las reglas de negocio RN-01 a RN-09 "
                        + "descritas en el diseño funcional.")
                .contact(new Contact()
                        .name("Diego Andre Rodriguez")
                        .url("https://github.com/DiegoAndreRodriguez/customer-account-service")));
    }
}