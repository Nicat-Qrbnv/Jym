--liquibase formatted sql

--changeset nicat:006-seed-data
INSERT INTO training_types (id, type_name)
VALUES (1, 'Technical'),
       (2, 'Non-technical');

INSERT INTO users (id, first_name, last_name, username, password, is_active)
VALUES (1, 'Oliver', 'Bennett', 'oliver.bennett',
        '$2a$10$C2u6ZsSIJv5PBfwao5HW/.zpntyJGszREUxcPsbyNw.BpLz5.hYr2', TRUE),
       (2, 'Emma', 'Carter', 'emma.carter',
        '$2a$10$VDVvYRDwx1m4pCeD.3bPB.Qod5G4M/hXwuhqNbtU1X605ufUOCM22', TRUE),
       (3, 'William', 'Hughes', 'william.hughes',
        '$2a$10$6ekYW3fGG2qM62G4QaRBvObZnkjFclLkQcs1p.FaERurRgNgj2T52', TRUE),
       (4, 'Sophia', 'Turner', 'sophia.turner',
        '$2a$10$ESQ7j11FLC53L.H6f8FOreT3mHJ1X/9u8mr3HS7T.x2RZwSgtKKlq', TRUE),
       (5, 'James', 'Parker', 'james.parker',
        '$2a$10$prEtdqd49HWXFU/tBltZNOxj0o8GZtB6EsiFpC0opzovUv9NkVR52', TRUE),
       (6, 'Charlotte', 'Reed', 'charlotte.reed',
        '$2a$10$Fv4KNDCyJK1MRVNUGbLYAu1RZpUCzEIZovmzuXPRIzITKQtnd5Ssy', TRUE),
       (7, 'Thomas', 'Brooks', 'thomas.brooks',
        '$2a$10$.gPBceAPgZIiACA7Yp7h1uIyVDDAZmmTv8yZcXB.0aH3KE8qKDzhW', TRUE),
       (8, 'Lily', 'Harrison', 'lily.harrison',
        '$2a$10$xd0jC0bUMprd9LeRut0kjOcfX2obioPZCskpo91B8hQmdIpdjslJW', TRUE),
       (9, 'Henry', 'Collins', 'henry.collins',
        '$2a$10$lNdte1gUi5hB74YXkid8JuDvfQ01BvnaLaM3SRFZNd61lpnmn2D4q', TRUE),
       (10, 'Amelia', 'Foster', 'amelia.foster',
        '$2a$10$Zbl1zwp7T51NDnhMYZvqxOQkHZaIWtMZP/tiibduJzNeKcJcDsLV.', TRUE),
       (11, 'Daniel', 'Morgan', 'daniel.morgan',
        '$2a$10$3LqHQ2kVkbXq9HJxcfDm2Oy0Af/wuUfhNfzJG2HzypxsygNOZf8iS', TRUE),
       (12, 'Grace', 'Walker', 'grace.walker',
        '$2a$10$fVXcdI3jCv6A0vlKmSf5F.0I.GUK08yxqF7iQ925VRh9mHw7lHAQ2', TRUE);

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

INSERT INTO trainings (id, name, training_type_id, trainee_id, trainer_id, scheduled_date,
                       duration_in_minutes)
VALUES (1, 'Java Basics', 1, 1, 1, '2026-01-12', 60),
       (2, 'Spring Boot Fundamentals', 1, 2, 1, '2026-01-15', 90),
       (3, 'Presentation Skills', 2, 3, 2, '2026-01-19', 60),
       (4, 'Team Collaboration', 2, 4, 2, '2026-01-22', 75),
       (5, 'Database Design', 1, 5, 3, '2026-01-26', 90),
       (6, 'Docker Workshop', 1, 6, 3, '2026-01-29', 90),
       (7, 'Business Communication', 2, 7, 4, '2026-02-02', 60),
       (8, 'Conflict Management', 2, 8, 4, '2026-02-05', 75);

ALTER TABLE training_types
    ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 13;
ALTER TABLE trainees
    ALTER COLUMN id RESTART WITH 9;
ALTER TABLE trainers
    ALTER COLUMN id RESTART WITH 5;
ALTER TABLE trainings
    ALTER COLUMN id RESTART WITH 9;

--rollback DELETE FROM trainings WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8);
--rollback DELETE FROM trainers WHERE id IN (1, 2, 3, 4);
--rollback DELETE FROM trainees WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8);
--rollback DELETE FROM users WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
--rollback DELETE FROM training_types WHERE id IN (1, 2);

