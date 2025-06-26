INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (1, 'admin', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'admin@example.com', 'Alvaro', 'Sanchez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (2, 'user', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'user@example.com', 'Luis', 'Rodriguez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (3, 'alvaro', '$2a$12$8GEPY51IGx0gNkErYnrF2eBUZWEgVR4gdnoPOFYwA2QYDvLj73Vwq', 'alvaro@example.com', 'Alvaro', 'Rodriguez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO customers (user_id, name, surname, company_name, email, phone, nif) VALUES (1, 'Cliente Ejemplo', 'Sanchez','Company, S.L.', 'cliente@example.com', '123-456-7890', '48769398M');
INSERT INTO customers (user_id, name, surname, company_name, email, phone, nif) VALUES (1, 'Cliente Segundo', 'Garcia','Legacy, S.A.', 'cliente2@example.com', '123-456-7890', '5669398M');
-- Añade más sentencias INSERT según tus necesidades