CREATE TABLE customers (
    id            BIGSERIAL PRIMARY KEY,
    external_id   VARCHAR(64)  NOT NULL,
    joined_at     DATE         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_customers_external_id UNIQUE (external_id)
);

CREATE TABLE customer_types (
    customer_id   BIGINT       NOT NULL,
    type          VARCHAR(32)  NOT NULL,
    CONSTRAINT pk_customer_types PRIMARY KEY (customer_id, type),
    CONSTRAINT fk_customer_types_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    CONSTRAINT ck_customer_types_type
        CHECK (type IN ('EMPLOYEE', 'AFFILIATE'))
);