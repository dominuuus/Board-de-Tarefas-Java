--liquibase formatted sql
--changeset domingos:202504052041
--comment: boards table create

CREATE TABLE BOARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS;