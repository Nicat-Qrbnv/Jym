--liquibase formatted sql

--changeset nicat:007-hash-seeded-user-passwords
UPDATE users
SET password = CASE username
                   WHEN 'oliver.bennett'
                       THEN '$2a$10$C2u6ZsSIJv5PBfwao5HW/.zpntyJGszREUxcPsbyNw.BpLz5.hYr2'
                   WHEN 'emma.carter'
                       THEN '$2a$10$VDVvYRDwx1m4pCeD.3bPB.Qod5G4M/hXwuhqNbtU1X605ufUOCM22'
                   WHEN 'william.hughes'
                       THEN '$2a$10$6ekYW3fGG2qM62G4QaRBvObZnkjFclLkQcs1p.FaERurRgNgj2T52'
                   WHEN 'sophia.turner'
                       THEN '$2a$10$ESQ7j11FLC53L.H6f8FOreT3mHJ1X/9u8mr3HS7T.x2RZwSgtKKlq'
                   WHEN 'james.parker'
                       THEN '$2a$10$prEtdqd49HWXFU/tBltZNOxj0o8GZtB6EsiFpC0opzovUv9NkVR52'
                   WHEN 'charlotte.reed'
                       THEN '$2a$10$Fv4KNDCyJK1MRVNUGbLYAu1RZpUCzEIZovmzuXPRIzITKQtnd5Ssy'
                   WHEN 'thomas.brooks'
                       THEN '$2a$10$.gPBceAPgZIiACA7Yp7h1uIyVDDAZmmTv8yZcXB.0aH3KE8qKDzhW'
                   WHEN 'lily.harrison'
                       THEN '$2a$10$xd0jC0bUMprd9LeRut0kjOcfX2obioPZCskpo91B8hQmdIpdjslJW'
                   WHEN 'henry.collins'
                       THEN '$2a$10$lNdte1gUi5hB74YXkid8JuDvfQ01BvnaLaM3SRFZNd61lpnmn2D4q'
                   WHEN 'amelia.foster'
                       THEN '$2a$10$Zbl1zwp7T51NDnhMYZvqxOQkHZaIWtMZP/tiibduJzNeKcJcDsLV.'
                   WHEN 'daniel.morgan'
                       THEN '$2a$10$3LqHQ2kVkbXq9HJxcfDm2Oy0Af/wuUfhNfzJG2HzypxsygNOZf8iS'
                   WHEN 'grace.walker'
                       THEN '$2a$10$fVXcdI3jCv6A0vlKmSf5F.0I.GUK08yxqF7iQ925VRh9mHw7lHAQ2'
                   ELSE password
    END
WHERE username IN (
                   'oliver.bennett',
                   'emma.carter',
                   'william.hughes',
                   'sophia.turner',
                   'james.parker',
                   'charlotte.reed',
                   'thomas.brooks',
                   'lily.harrison',
                   'henry.collins',
                   'amelia.foster',
                   'daniel.morgan',
                   'grace.walker'
    )
  AND password NOT LIKE '$2%';

--rollback UPDATE users SET password = 'A7mQ2zLp9R' WHERE username = 'oliver.bennett';
--rollback UPDATE users SET password = 'B8nT5xY0pK' WHERE username = 'emma.carter';
--rollback UPDATE users SET password = 'C4vN9sK2lA' WHERE username = 'william.hughes';
--rollback UPDATE users SET password = 'D6rF1qW8eZ' WHERE username = 'sophia.turner';
--rollback UPDATE users SET password = 'E3hJ7uM5cV' WHERE username = 'james.parker';
--rollback UPDATE users SET password = 'F9kL0bN2yS' WHERE username = 'charlotte.reed';
--rollback UPDATE users SET password = 'L4zN0rFy9C' WHERE username = 'thomas.brooks';
--rollback UPDATE users SET password = 'M7xD5kPu1Q' WHERE username = 'lily.harrison';
--rollback UPDATE users SET password = 'G2pR8xVa4M' WHERE username = 'henry.collins';
--rollback UPDATE users SET password = 'H5qW1nJz7B' WHERE username = 'amelia.foster';
--rollback UPDATE users SET password = 'J9cT3yLp6D' WHERE username = 'daniel.morgan';
--rollback UPDATE users SET password = 'K1vB8mQs2E' WHERE username = 'grace.walker';
