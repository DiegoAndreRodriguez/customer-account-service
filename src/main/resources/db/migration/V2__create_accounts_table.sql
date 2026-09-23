CREATE SEQUENCE account_number_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE accounts (
                          id             UUID           NOT NULL,
                          account_number VARCHAR(14)    NOT NULL,
                          account_type   VARCHAR(20)    NOT NULL,
                          currency       VARCHAR(3)     NOT NULL,
                          balance        NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                          status         VARCHAR(20)    NOT NULL,
                          customer_id    UUID           NOT NULL,
                          created_at     TIMESTAMP      NOT NULL,
                          updated_at     TIMESTAMP      NOT NULL,
                          CONSTRAINT pk_accounts PRIMARY KEY (id),
                          CONSTRAINT uk_accounts_account_number UNIQUE (account_number),
                          CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
                          CONSTRAINT ck_accounts_type CHECK (account_type IN ('SAVINGS', 'CHECKING')),
                          CONSTRAINT ck_accounts_currency CHECK (currency IN ('PEN', 'USD')),
                          CONSTRAINT ck_accounts_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
                          CONSTRAINT ck_accounts_balance CHECK (balance >= 0)
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);

COMMENT ON TABLE accounts IS 'Cuentas bancarias asociadas a un cliente';