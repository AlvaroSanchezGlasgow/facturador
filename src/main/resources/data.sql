INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (1, 'admin', '$2a$12$3dxvHvImrI82YowezCgZ2OA2ISvFZicIU3z4rtmrBgvuxJG241p4K', 'admin@example.com', 'Alvaro', 'Sanchez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO users (id, username, password, email, first_name, last_name, company_name, tax_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, version)
VALUES (2, 'user', '$2a$12$Wb404pxIXcFOGCKzNW05I.YcR7elDbh6x4OLFxzdZvcSq9JJDbzqG', 'user@example.com', 'Luis', 'Rodriguez', 'Netquest',1, true, true, true, true, 1);

INSERT INTO customers (id, user_id, name, email, phone) VALUES (1,1, 'Cliente Ejemplo', 'cliente@example.com', '123-456-7890');
-- Añade más sentencias INSERT según tus necesidades