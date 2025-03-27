INSERT INTO tb_user (name, email, password_hash, last_image_processed, process_count, subscription_plan)
VALUES
    ('admin', 'admin@test.com', '$2a$10$GBqbZRxDMPiu4nZMoqBz/eRUTRRfNKIxQGUJHi7k1Ld5Z8QWyP7xq', NULL, 0, 'BASIC'),
    ('user', 'user@test.com', '$2a$10$GBqbZRxDMPiu4nZMoqBz/eRUTRRfNKIxQGUJHi7k1Ld5Z8QWyP7xq', NULL, 0, 'BASIC');
