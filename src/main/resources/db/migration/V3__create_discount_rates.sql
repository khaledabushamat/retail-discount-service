CREATE TABLE discount_rates (
    type        VARCHAR(32)   PRIMARY KEY,
    percentage  NUMERIC(5,2)  NOT NULL,
    CONSTRAINT ck_discount_rates_percentage
        CHECK (percentage >= 0 AND percentage <= 100)
);

INSERT INTO discount_rates (type, percentage) VALUES
    ('EMPLOYEE',  30.00),
    ('AFFILIATE', 10.00),
    ('LOYALTY',    5.00);