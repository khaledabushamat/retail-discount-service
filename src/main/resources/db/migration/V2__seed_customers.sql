INSERT INTO customers (external_id, joined_at) VALUES
    ('emp-001',    DATE '2020-01-15'),
    ('aff-001',    DATE '2024-06-01'),
    ('loyal-001',  DATE '2021-03-10'),
    ('new-001',    DATE '2025-11-20');

INSERT INTO customer_types (customer_id, type)
SELECT id, 'EMPLOYEE'  FROM customers WHERE external_id = 'emp-001';

INSERT INTO customer_types (customer_id, type)
SELECT id, 'AFFILIATE' FROM customers WHERE external_id = 'aff-001';