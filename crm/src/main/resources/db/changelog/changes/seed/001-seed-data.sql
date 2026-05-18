--liquibase formatted sql

--changeset nicat:006-seed-data
INSERT INTO training_types (id, name)
VALUES (1, 'Technical'),
       (2, 'Non-technical');

INSERT INTO users (id, first_name, last_name, username, password, is_active)
VALUES (1, 'Oliver', 'Bennett', 'oliver.bennett', 'A7mQ2zLp9R', TRUE),
       (2, 'Emma', 'Carter', 'emma.carter', 'B8nT5xY0pK', TRUE),
       (3, 'William', 'Hughes', 'william.hughes', 'C4vN9sK2lA', TRUE),
       (4, 'Sophia', 'Turner', 'sophia.turner', 'D6rF1qW8eZ', TRUE),
       (5, 'James', 'Parker', 'james.parker', 'E3hJ7uM5cV', TRUE),
       (6, 'Charlotte', 'Reed', 'charlotte.reed', 'F9kL0bN2yS', TRUE),
       (7, 'Thomas', 'Brooks', 'thomas.brooks', 'L4zN0rFy9C', TRUE),
       (8, 'Lily', 'Harrison', 'lily.harrison', 'M7xD5kPu1Q', TRUE),
       (9, 'Henry', 'Collins', 'henry.collins', 'G2pR8xVa4M', TRUE),
       (10, 'Amelia', 'Foster', 'amelia.foster', 'H5qW1nJz7B', TRUE),
       (11, 'Daniel', 'Morgan', 'daniel.morgan', 'J9cT3yLp6D', TRUE),
       (12, 'Grace', 'Walker', 'grace.walker', 'K1vB8mQs2E', TRUE);

INSERT INTO trainees (id, user_id, date_of_birth, address)
VALUES (1, 1, '2000-01-01', 'Baku'),
       (2, 2, '2000-01-01', 'Baku'),
       (3, 3, '2000-01-01', 'Baku'),
       (4, 4, '2000-01-01', 'Baku'),
       (5, 5, '2000-01-01', 'Baku'),
       (6, 6, '2000-01-01', 'Baku'),
       (7, 7, '2000-01-01', 'Baku'),
       (8, 8, '2000-01-01', 'Baku');

INSERT INTO trainers (id, user_id, specialization_id)
VALUES (1, 9, 1),
       (2, 10, 2),
       (3, 11, 1),
       (4, 12, 2);

INSERT INTO trainings (id, name, training_type_id, trainee_id, trainer_id, scheduled_date, duration_in_minutes)
VALUES (1, 'Java Basics', 1, 1, 1, '2026-01-12', 60),
       (2, 'Spring Boot Fundamentals', 1, 2, 1, '2026-01-15', 90),
       (3, 'Presentation Skills', 2, 3, 2, '2026-01-19', 60),
       (4, 'Team Collaboration', 2, 4, 2, '2026-01-22', 75),
       (5, 'Database Design', 1, 5, 3, '2026-01-26', 90),
       (6, 'Docker Workshop', 1, 6, 3, '2026-01-29', 90),
       (7, 'Business Communication', 2, 7, 4, '2026-02-02', 60),
       (8, 'Conflict Management', 2, 8, 4, '2026-02-05', 75);

ALTER TABLE training_types ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users ALTER COLUMN id RESTART WITH 13;
ALTER TABLE trainees ALTER COLUMN id RESTART WITH 9;
ALTER TABLE trainers ALTER COLUMN id RESTART WITH 5;
ALTER TABLE trainings ALTER COLUMN id RESTART WITH 9;

--rollback DELETE FROM trainings WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8);
--rollback DELETE FROM trainers WHERE id IN (1, 2, 3, 4);
--rollback DELETE FROM trainees WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8);
--rollback DELETE FROM users WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
--rollback DELETE FROM training_types WHERE id IN (1, 2);

