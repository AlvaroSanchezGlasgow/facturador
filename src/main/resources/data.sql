INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (1, 'admin', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'admin@example.com', 'Alvaro', 'Sanchez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (2, 'user', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'user@example.com', 'Luis', 'Rodriguez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (3, 'alvaro', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'alvaro@example.com', 'Alvaro', 'Rodriguez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO customers (id, user_id, name, surname, company_name, email, phone, nif, version, created_at, updated_at)
VALUES (1, 3, 'Cliente Ejemplo', 'Sanchez','Company, S.L.', 'cliente@example.com', '123-456-7890', '48769398M', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO customers (id, user_id, name, surname, company_name, email, phone, nif, version, created_at, updated_at)
VALUES (2, 3, 'Cliente Segundo', 'Garcia','Legacy, S.A.', 'cliente2@example.com', '123-456-7890', '5669398M', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- Añade más sentencias INSERT según tus necesidades

-- Reset auto increment counters to avoid duplicate key errors
ALTER TABLE customers ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;
