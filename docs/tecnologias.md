# Stack Tecnológico — Customer Account Service

**Proyecto:** `customer-account-service`
**Autor:** Diego Andre Rodriguez
**Versión:** 1.0
**Fecha:** Septiembre 2026

---

## 1. Propósito

Este documento describe las tecnologías utilizadas en el proyecto, **para qué** se usa cada una y **por qué** fue elegida. Complementa al [Diseño de Componentes](./02-diseno-componentes.md), donde se detallan las decisiones de arquitectura.

---

## 2. Lenguaje y framework

| Tecnología | Para qué se usó | Por qué |
|---|---|---|
| **Java 25** | Lenguaje de todo el servicio. | Es la versión **LTS** (soporte a largo plazo), el estándar en entornos corporativos y bancarios. Se descartó Java 26 por no ser LTS. Se aprovechan los *records* para los DTOs. |
| **Spring Boot 4.1** | Base de la aplicación: arranque, configuración e inyección de dependencias. | Framework de referencia para microservicios en Java. Incluye servidor embebido y autoconfiguración, lo que reduce el código repetitivo. |
| **Spring Web MVC** | Controladores REST (`@RestController`) que exponen los endpoints `/api/v1/...`. | Forma estándar de construir APIs HTTP en Spring. |
| **Spring Validation** | Validación de los datos de entrada: DNI de 8 dígitos, correo válido, campos obligatorios. | Rechaza los datos inválidos con **400** antes de que lleguen a la lógica de negocio e indica qué campo falló. |
| **Maven** (con *wrapper* `mvnw`) | Compilación, gestión de dependencias y ejecución de pruebas. | El *wrapper* permite compilar sin instalar Maven y garantiza que todos usen la misma versión. |

---

## 3. Persistencia

| Tecnología | Para qué se usó | Por qué |
|---|---|---|
| **PostgreSQL 16** | Base de datos donde se almacenan clientes y cuentas. | Base de datos relacional, robusta y de código abierto. Soporta **restricciones** (`UNIQUE`, `CHECK`, `FOREIGN KEY`), de modo que las reglas de negocio también se protegen en la base de datos. Ofrece **secuencias** para generar números de cuenta sin repetición y tipo nativo UUID. |
| **Spring Data JPA / Hibernate** | Mapeo entre objetos Java y tablas: guardar, buscar y paginar. | Evita escribir SQL manual para el CRUD. Las consultas se derivan del nombre del método (por ejemplo, `existsByEmail`). Su uso queda confinado a la capa de infraestructura. |
| **Flyway** | Creación y versionado del esquema de base de datos mediante scripts SQL (`V1__create_customers_table.sql`, `V2__create_accounts_table.sql`). | Cada cambio queda **versionado y es reproducible**: la base de datos es idéntica en local, en CI y en la nube. Se descartó que Hibernate genere las tablas (`ddl-auto`), por ser riesgoso en producción; Hibernate solo **valida** que el código coincida con el esquema. |

---

## 4. Documentación de la API

| Tecnología | Para qué se usó | Por qué |
|---|---|---|
| **springdoc-openapi** (OpenAPI 3.1 + Swagger UI) | Generación de la documentación de los endpoints y de la interfaz interactiva `/swagger-ui.html`. | La documentación se genera **desde el código**, por lo que no queda desactualizada. Permite probar la API desde el navegador sin herramientas adicionales. |

---

## 5. Pruebas

| Tecnología | Para qué se usó | Por qué |
|---|---|---|
| **JUnit 5** | Framework base de las pruebas. | Estándar de pruebas en Java. |
| **Mockito** | Simulación de los puertos (repositorios) en las pruebas de reglas de negocio. | Permite probar las reglas **sin base de datos** y en milisegundos, gracias a la arquitectura hexagonal. |
| **MockMvc** | Pruebas de los controladores: códigos HTTP (201, 400, 404, 409) y formato de error. | Verifica el contrato de la API sin levantar un servidor real. |
| **AssertJ** | Escritura de las verificaciones de las pruebas. | Ofrece una sintaxis fluida y legible (`assertThat(x).isEqualTo(y)`). |

---

## 6. Contenedores, integración continua y nube

| Tecnología | Para qué se usó | Por qué |
|---|---|---|
| **Docker** (Dockerfile multi-etapa) | Empaquetado de la aplicación en una imagen: la etapa 1 compila con el JDK y la etapa 2 solo ejecuta con el JRE. | Garantiza el mismo comportamiento en cualquier máquina. La imagen final es **más liviana y segura** (no incluye el compilador) y se ejecuta con un usuario sin privilegios de root. |
| **Docker Compose** | Levantar la aplicación y PostgreSQL juntos con un solo comando (`docker compose up`). | Quien clone el repositorio solo necesita Docker; no requiere instalar Java ni PostgreSQL. |
| **Git + GitHub** | Control de versiones y repositorio público del código. | Los commits siguen la convención *Conventional Commits* (`feat:`, `fix:`, `docs:`), de modo que el historial refleja la evolución del proyecto. |
| **GitHub Actions** | Integración continua: en cada *push* a `master` compila y ejecuta todas las pruebas contra un PostgreSQL real. | Detecta errores de forma automática antes de que lleguen a producción. El *badge* del README muestra el estado del build. |
| **Render** | Plataforma en la nube donde corre el servicio (aplicación + PostgreSQL gestionado). | Ofrece **plan gratuito**, despliega directamente desde GitHub usando el Dockerfile y provee PostgreSQL administrado. Verifica `/actuator/health` antes de dirigir tráfico a una nueva versión. Limitación: el servicio se suspende tras un periodo sin uso y tarda cerca de un minuto en reactivarse. |
| **Spring Boot Actuator** | Endpoint `/actuator/health`, que indica si la aplicación está operativa. | Render lo utiliza para confirmar que un despliegue fue exitoso. Sienta la base para la observabilidad. |

---

## 7. Resumen

Se eligieron tecnologías estándar de la industria: **Java y Spring Boot** para el servicio, **PostgreSQL con Flyway** para datos confiables y versionados, **Docker** para una ejecución idéntica en cualquier entorno, **GitHub Actions** para validar automáticamente cada cambio y **Render** para el despliegue en la nube sin costo.

---

**Documentos relacionados:** [Diseño Funcional](./01-diseno-funcional.md) · [Diseño de Componentes](./02-diseno-componentes.md)
