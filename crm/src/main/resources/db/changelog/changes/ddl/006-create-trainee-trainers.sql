--liquibase formatted sql

--changeset nicat:006-create-trainee-trainers
CREATE TABLE trainee_trainers
(
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    CONSTRAINT pk_trainee_trainers PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_trainee_trainers_trainee_id
        FOREIGN KEY (trainee_id) REFERENCES trainees (id) ON DELETE CASCADE,
    CONSTRAINT fk_trainee_trainers_trainer_id
        FOREIGN KEY (trainer_id) REFERENCES trainers (id) ON DELETE CASCADE
);

--rollback DROP TABLE trainee_trainers;
