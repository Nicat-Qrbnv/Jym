--liquibase formatted sql

--changeset nicat:007-seed-trainee-trainers
INSERT INTO trainee_trainers (trainee_id, trainer_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 3),
       (6, 3),
       (7, 4),
       (8, 4);

--rollback DELETE FROM trainee_trainers WHERE trainee_id IN (1, 2, 3, 4, 5, 6, 7, 8);
