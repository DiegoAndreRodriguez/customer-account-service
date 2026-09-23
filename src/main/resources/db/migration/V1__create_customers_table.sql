CREATE TABLE customers (
                           id              UUID         NOT NULL,
                           document_number VARCHAR(8)   NOT NULL,
                           first_name      VARCHAR(100) NOT NULL,
                           last_name       VARCHAR(100) NOT NULL,
                           email           VARCHAR(150) NOT NULL,
                           phone           VARCHAR(15),
                           status          VARCHAR(20)  NOT NULL,
                           created_at      TIMESTAMP    NOT NULL,
                           updated_at      TIMESTAMP    NOT NULL,
                           CONSTRAINT pk_customers PRIMARY KEY (id),
                           CONSTRAINT uk_customers_document_number UNIQUE (document_number),
                           CONSTRAINT uk_customers_email UNIQUE (email),
                           CONSTRAINT ck_customers_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_customers_status ON customers (status);

COMMENT ON TABLE customers IS 'Datos maestros de los clientes del banco';